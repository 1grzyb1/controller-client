package ovh.snet.grzybek.controller.client.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ControllerClientExample {

  public static void main(String[] args) {
    new SpringApplicationBuilder(ControllerClientExample.class).run(args);
  }
}
