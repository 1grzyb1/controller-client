package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class UploadFileExamples {

  @Autowired private ControllerClientFactory controllerClientFactory;

  private ExampleController exampleController;

  @BeforeEach
  void setUp() {
    exampleController = controllerClientFactory.create(ExampleController.class);
  }

  @Test
  void uploadFile() {
    var file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
    var response = exampleController.uploadFile(file);
    assertThat(response.message()).isEqualTo("Uploaded File: test.txt (text/plain)");
  }

  @Test
  void uploadFileWithPutRequest() {
    var file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello, World!".getBytes());
    var response = exampleController.uploadFilePut(file);
    assertThat(response.message()).isEqualTo("Uploaded File using put: test.txt (text/plain)");
  }

  @Test
  void sendStreamRequest() {
    String content = "test";
    InputStream inputStream = new ByteArrayInputStream(content.getBytes());
    String response = exampleController.streamFile(inputStream);
    assertThat(response).isEqualTo("Received stream");
  }
}
