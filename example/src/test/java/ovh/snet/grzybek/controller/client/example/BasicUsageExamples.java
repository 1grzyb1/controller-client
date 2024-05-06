package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import ovh.snet.grzybek.controller.client.core.ControllerClientBuilderFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
//@Import(ControllerClientBuilderFactory.class)
@AutoConfigureMockMvc
class BasicUsageExamples {

  @Autowired private ControllerClientBuilderFactory controllerClientBuilderFactory;

  private ExampleController exampleController;

  @BeforeEach
  void setUp() {
    exampleController = controllerClientBuilderFactory.builder(ExampleController.class).build();
  }

  @Test
  void basicGet() {
    var response = exampleController.exampleMethod();
    assertThat(response.message()).isEqualTo("Hello world!");
  }

  @Test
  void postWithBody() {
    var request = new ExampleRequest("Test message");
    var response = exampleController.bodyExample(request);
    assertThat(response.message()).isEqualTo("Received: Test message");
  }

  @Test
  void getWithParam() {
    var response = exampleController.paramExample("param");
    assertThat(response.message()).isEqualTo("Received: param");
  }

  @Test
  void getWithPath() {
    var response = exampleController.pathExample("Test path");
    assertThat(response.message()).isEqualTo("Received: Test path");
  }

  @Test
  void getList() {
    var response = exampleController.listExample();
    assertThat(response).hasSize(2);
    assertThat(response.get(0).message()).isEqualTo("a");
    assertThat(response.get(1).message()).isEqualTo("b");
  }

  @Test
  void getParametrizedType() {
    var response = exampleController.twoType();
    assertThat(response.getFirst()).isEqualTo("a");
    assertThat(response.getSecond()).isEqualTo(1);
  }

  @Test
  void uploadFile() {
    var file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
    var response = exampleController.uploadFile(file);
    assertThat(response.message()).isEqualTo("Uploaded File: test.txt (text/plain)");
  }
}
