package antifraud.controller.json;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feedback {
    private Long transactionId;
    private String feedback;
}
