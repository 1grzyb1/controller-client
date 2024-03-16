package ovh.snet.grzybek.controller.client.example;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/example", produces = APPLICATION_JSON_VALUE)
class ExampleController {

    @GetMapping
    ExampleResponse exampleMethod() {
        return new ExampleResponse("Hello world!");
    }
}
