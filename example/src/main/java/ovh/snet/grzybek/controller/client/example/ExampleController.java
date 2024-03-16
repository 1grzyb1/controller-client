package ovh.snet.grzybek.controller.client.example;


import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/example", produces = APPLICATION_JSON_VALUE)
class ExampleController {

    @GetMapping
    ExampleResponse exampleMethod() {
        return new ExampleResponse("Hello world!");
    }

    @PostMapping("/body")
    public ExampleResponse bodyExample(@RequestBody ExampleRequest request) {
        return new ExampleResponse("Received: " + request.message());
    }

    @GetMapping("/param")
    public ExampleResponse paramExample(@RequestParam String message) {
        return new ExampleResponse("Received: " + message);
    }

    @GetMapping("/path/{message}")
    public ExampleResponse pathExample(@PathVariable("message") String message) {
        return new ExampleResponse("Received: " + message);
    }
}
