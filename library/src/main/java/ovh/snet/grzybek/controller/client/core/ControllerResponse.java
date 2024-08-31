package ovh.snet.grzybek.controller.client.core;

import org.springframework.mock.web.MockHttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ControllerResponse<T>(long contentLength, String contentType,
                                    Map<String, List<String>> headers, String errorMessage, int statusCode,
                                    T response) {

    ControllerResponse(MockHttpServletResponse response) {
        this(response, null);
    }

    ControllerResponse(MockHttpServletResponse response, T responseBody) {
        this(response.getContentLength(),
                response.getContentType(),
                getHeaders(response),
                response.getErrorMessage(),
                response.getStatus(),
                responseBody);
    }

    private static Map<String, List<String>> getHeaders(MockHttpServletResponse response) {
        return response.getHeaderNames().stream()
                .collect(HashMap::new, (map, name) -> map.put(name, response.getHeaders(name)), Map::putAll);
    }
}
