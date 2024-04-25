package ovh.snet.grzybek.controller.client.example;

public record ResponseWithEnum(String something, ExmapleEnum exmapleEnum) {

    enum ExmapleEnum {
        VALUE,
        ANOTHER_VALUE
    }
}
