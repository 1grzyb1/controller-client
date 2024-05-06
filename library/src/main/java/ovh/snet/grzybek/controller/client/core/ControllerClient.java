package ovh.snet.grzybek.controller.client.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class ControllerClient<T> {

    private final Class<?> clazz;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final List<Consumer<RequestBuilder>> requestCustomizers;
    private final List<Function<ResultActions, ResultActions>> resultCustomizers;

    public ControllerClient(
            Class<?> clazz,
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            List<Consumer<RequestBuilder>> requestCustomizers,
            List<Function<ResultActions, ResultActions>> resultCustomizers) {
        this.clazz = clazz;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.requestCustomizers = requestCustomizers;
        this.resultCustomizers = resultCustomizers;
    }

    T getClient() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> intercept(method, args));
        return (T) enhancer.create(getParameterTypes(), getConstructorParams());
    }

    private Object intercept(Method method, Object[] args) throws Exception {
        var requestBuilder = prepareRequest(method, args);

        requestCustomizers.forEach(customizer -> customizer.accept(requestBuilder));
        var perform = mockMvc.perform(requestBuilder);
        resultCustomizers.forEach(customizer -> customizer.apply(perform));

        var response = perform.andReturn().getResponse();

        var returnType = method.getGenericReturnType();
        if (returnType.equals(Void.TYPE)) {
            return null;
        }

        if (response.getStatus() < 200 || response.getStatus() >= 300) {
            return null;
        }

        if (returnType instanceof ParameterizedType) {
            return mapParameterizedType((ParameterizedType) returnType, response);
        } else if (returnType instanceof Class) {
            return objectMapper.readValue(response.getContentAsString(), (Class<?>) returnType);
        }

        throw new UnsupportedOperationException("Unsupported return type: " + returnType);
    }

    private Object mapParameterizedType(
            ParameterizedType returnType, MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        var rawType = (Class<?>) returnType.getRawType();

        var actualTypeArguments = returnType.getActualTypeArguments();

        var typeArgumentsClasses =
                Arrays.stream(actualTypeArguments)
                        .map(ControllerClient::getaClass)
                        .toArray(Class<?>[]::new);

        var javaType =
                objectMapper.getTypeFactory().constructParametricType(rawType, typeArgumentsClasses);

        return objectMapper.readValue(response.getContentAsString(), javaType);
    }

    private static Class<?> getaClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        throw new UnsupportedOperationException("Unsupported type argument: " + type.getTypeName());
    }

    private MockHttpServletRequestBuilder prepareRequest(Method method, Object[] args) {
        var classAnnotations = clazz.getAnnotations();
        var requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        var requestPath = requestMapping.path();
        var endpoint =
                getBaseUrl(classAnnotations) + (requestPath.length > 0 ? requestMapping.path()[0] : "");
        endpoint = setPathVariables(method, args, endpoint);
        var requestBuilder = getRequestBuilder(method, requestMapping, endpoint);
        setRequestParams(method, args, requestBuilder);
        setRequestBody(method, args, requestBuilder);
        return requestBuilder;
    }

    private static MockHttpServletRequestBuilder getRequestBuilder(Method method, RequestMapping requestMapping, String endpoint) {
        if (isMultipart(method)) {
            return MockMvcRequestBuilders.multipart(endpoint);
        }
        return MockMvcRequestBuilders.request(requestMapping.method()[0].asHttpMethod(), endpoint);
    }


    private static boolean isMultipart(Method method) {
        return Arrays.stream(method.getParameterTypes())
                .anyMatch(InputStreamSource.class::isAssignableFrom);
    }

    private void setRequestBody(
            Method method, Object[] args, MockHttpServletRequestBuilder requestBuilder) {
        var requestBody = getRequestBodyArg(method, args);
        requestBody.ifPresent(
                body -> {
                    try {
                        requestBuilder
                                .content(objectMapper.writeValueAsString(body))
                                .contentType(MediaType.APPLICATION_JSON);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private static void setRequestParams(
            Method method, Object[] args, MockHttpServletRequestBuilder requestBuilder) {
        var queryParams = getRequestParams(method, args);
        queryParams.forEach((key, value) -> {
            if (value instanceof MockMultipartFile) {
                ((MockMultipartHttpServletRequestBuilder) requestBuilder).file((MockMultipartFile) value);
            } else {
                requestBuilder.param(key, value.toString());
            }
        });
    }

    private static Map<String, Object> getRequestParams(Method method, Object[] args) {
        var parameters = method.getParameters();
        return Arrays.stream(parameters)
                .filter(param -> param.getAnnotation(RequestParam.class) != null)
                .collect(
                        Collectors.toMap(
                                ControllerClient::getQueryParamKey,
                                param -> getQueryParamArg(args, param, parameters),
                                (existing, replacement) -> existing,
                                HashMap::new));
    }

    private static String setPathVariables(Method method, Object[] args, String endpoint) {
        var pathVariables = getPathVariable(method, args);
        for (var entry : pathVariables.entrySet()) {
            endpoint = endpoint.replace("{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return endpoint;
    }

    private Object[] getConstructorParams() {
        var parameterTypes = getParameterTypes();
        var parameterValues = new Object[parameterTypes.length];
        Arrays.fill(parameterValues, null);
        return parameterValues;
    }

    private Class<?>[] getParameterTypes() {
        var constructors = clazz.getDeclaredConstructors();
        var constructor = constructors[0];

        return constructor.getParameterTypes();
    }

    private static Optional<Object> getRequestBodyArg(Method method, Object[] args) {
        Annotation[][] paramAnnotations = method.getParameterAnnotations();

        return IntStream.range(0, paramAnnotations.length)
                .filter(
                        i ->
                                Arrays.stream(paramAnnotations[i])
                                        .anyMatch(annotation -> annotation instanceof RequestBody))
                .mapToObj(index -> args[index])
                .findFirst();
    }

    private static String getBaseUrl(Annotation[] classAnnotations) {
        return Arrays.stream(classAnnotations)
                .filter(annotation -> annotation instanceof RequestMapping)
                .map(annotation -> (RequestMapping) annotation)
                .map(requestMapping -> requestMapping.value()[0])
                .findFirst()
                .orElseThrow();
    }

    private static Map<String, Object> getPathVariable(Method method, Object[] args) {
        var parameters = method.getParameters();
        return Arrays.stream(parameters)
                .filter(param -> param.getAnnotation(PathVariable.class) != null)
                .collect(
                        Collectors.toMap(
                                ControllerClient::getPathVariableKey,
                                param -> getPathVariableArg(args, param, parameters),
                                (existing, replacement) -> existing,
                                HashMap::new));
    }

    private static Object getPathVariableArg(Object[] args, Parameter param, Parameter[] parameters) {
        return args[Arrays.asList(parameters).indexOf(param)];
    }

    private static String getPathVariableKey(Parameter param) {
        return param.getName();
    }

    private static Object getQueryParamArg(Object[] args, Parameter param, Parameter[] parameters) {
        return args[Arrays.asList(parameters).indexOf(param)];
    }

    private static String getQueryParamKey(Parameter param) {
        var requestParamAnnotation = param.getAnnotation(RequestParam.class);
        return requestParamAnnotation.value().isEmpty()
                ? param.getName()
                : requestParamAnnotation.value();
    }
}
