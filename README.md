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

## Installation

Not yet published to Maven Central. You can copy classes for now.

## Example Usage

### Basic Usage

Create a proxy instance of your controller and invoke methods directly:

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

### Customizing Requests

Add headers, query parameters, or modify the request in other ways:

``` java
  @Test
  void cusotmize() {
    var client =
        controllerClientBuilderFactory
            .builder(ExampleController.class)
            .customizeRequest(
                request ->
                    ((MockHttpServletRequestBuilder) request).header("Authorization", "token"))
            .expectStatus(HttpStatus.OK.value())
            .build();
    var response = client.exampleMethod();
    assertThat(response.message()).isEqualTo("Hello world!");
  }
```

### Advanced Scenario: Using Caller for Descriptive Tests

Leverage ControllerClientCaller for more descriptive and assertive test using Client caller:

``` java
    var clientCaller = controllerClientBuilderFactory.caller(ExampleController.class);
    var result = clientCaller
            .when(ExampleController::exampleMethod)
            .thenStatus(HttpStatus.OK.value())
            .execute(ExampleResponse.class);
```