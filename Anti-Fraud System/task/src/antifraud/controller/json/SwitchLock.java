package antifraud.controller.json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class SwitchLock {
    private String username;

    private String operation;
}
