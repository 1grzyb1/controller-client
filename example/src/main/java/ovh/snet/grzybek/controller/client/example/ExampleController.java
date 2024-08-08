package ovh.snet.grzybek.controller.client.example;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

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

    @GetMapping("/header")
    ExampleResponse headerExample(HttpServletRequest request) {
        var exampleHeader = request.getHeader("X-Example-Header");
        var message = "Header value: " + (exampleHeader != null ? exampleHeader : "Not Provided");
        return new ExampleResponse(message);
    }

    @GetMapping("/list")
    List<ExampleResponse> listExample() {
        return List.of(new ExampleResponse("a"), new ExampleResponse("b"));
    }

    @GetMapping("/twoType")
    TwoParameterType<String, Integer> twoType() {
        return new TwoParameterType<>("a", 1);
    }

    @GetMapping("/enum")
    ResponseEntity<ResponseWithEnum> enumExample() {
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/upload")
    public ExampleResponse uploadFile(@RequestParam("file") MultipartFile file) {
        var fileName = file.getOriginalFilename();
        var contentType = file.getContentType();
        return new ExampleResponse("Uploaded File: " + fileName + " (" + contentType + ")");
    }

    @PutMapping("/upload")
    public ExampleResponse uploadFilePut(@RequestParam("file") MultipartFile file) {
        var fileName = file.getOriginalFilename();
        var contentType = file.getContentType();
        return new ExampleResponse("Uploaded File using put: " + fileName + " (" + contentType + ")");
    }

    @PostMapping("/stream")
    public String streamFile(InputStream inputStream) {
        return "Received stream";
    }
}
