package ovh.snet.grzybek.controller.client.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import ovh.snet.grzybek.controller.client.core.ControllerClientFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class CallerConfigurationExamples {

  @Autowired private ControllerClientFactory controllerClientFactory;

  @Test
  void checkExpectedStatusUsingCaller() {
    var clientCaller = controllerClientFactory.caller(ExampleController.class);
    var result =
        (ExampleResponse)
            clientCaller
                .when(ExampleController::exampleMethod)
                .thenStatus(HttpStatus.OK.value())
                .execute();

    assertThat(result.message()).isEqualTo("Hello world!");
  }

  @Test
  void checkInvalidStatusWhenEnumInResponse() {
    var clientCaller = controllerClientFactory.caller(ExampleController.class);
    var result =
            (ExampleResponse)
                    clientCaller
                            .when(ExampleController::enumExample)
                            .thenStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .execute();
  }
}
