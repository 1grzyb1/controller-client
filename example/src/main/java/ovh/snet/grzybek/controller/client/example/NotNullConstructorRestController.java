package ovh.snet.grzybek.controller.client.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/not-null", produces = APPLICATION_JSON_VALUE)
public class NotNullConstructorRestController {

    public NotNullConstructorRestController(ExampleService exampleService) {
        if (exampleService == null) {
            throw new IllegalArgumentException("arg cannot be null");
        }
    }

    @GetMapping
    public String exampleMethod() {
        return "Hello world!";
    }
}
