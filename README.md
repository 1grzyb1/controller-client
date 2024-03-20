# Controller Client for Spring Boot: A Dynamic Proxy-Based Testing Library

## Overview

The Controller Client library is designed for Spring Boot applications to facilitate testing of REST Controllers in a
concise,
expressive, and type-safe manner.
It leverages dynamic proxies and the Spring MockMvc framework to create a seamless integration testing experience.
This library abstracts the boilerplate code required for setting up and executing requests against Spring MVC
controllers,
thus allowing developers to focus on testing the behavior of their applications.

## Key Features

- **Dynamic Proxy for Controllers**: Automatically generates proxy instances of your controller classes, allowing for direct
  method calls in tests.
- **Request and Response Customization**: Offers hooks for customizing requests and responses, enabling detailed control
  over test conditions and assertions.
- **Integrated Status Code Assertions**: Simplifies the process of asserting expected HTTP status codes in response to
  controller actions.
- **Support for Parameterized Requests**: Handles path variables, request parameters, and request bodies with ease, mapping
  them correctly to the underlying MockMvc request builder.
- **Type-Safe Response Handling**: Automatically maps JSON responses back to Java objects, supporting both single objects
  and lists, based on the controller method's return type.

This library is still in early stage, so it doesn't support all spring web annotations.

Currently supported annotations are:

- `PathVariable`
- `RequestBody`
- `RequestMapping` including annotations inheriting it
- `RequestParam`

## Installation

1. Add the following dependency to your `build.gradle` project:
    ```
    implementation 'io.github.1grzyb1:controller-client:1.0.1'
    ```
2. Add `-parameters` to your Java compiler options to enable parameter names in the generated bytecode.
   For Gradle, you can do this by adding the following to your `build.gradle` file:
   ``` groovy
   compileJava {
      options.compilerArgs += ['-parameters']
   }
    ```

## Example Usage

### Basic Usage

Create a proxy instance of your controller and invoke methods directly

Underneath it uses `MockMvc` to perform the request and map the response to the return type of the method.

``` java
    @Autowired
    private ControllerClientBuilderFactory controllerClientBuilderFactory;
    
    @Test
    void basicGet() {
        ExampleController exampleController = controllerClientBuilderFactory.builder(ExampleController.class).build();
        var response = exampleController.exampleMethod();
        assertThat(response.message()).isEqualTo("Hello world!");
    }
```

You can check more examples in the [example package](example/src/test/java/ovh/snet/grzybek/controller/client/example).