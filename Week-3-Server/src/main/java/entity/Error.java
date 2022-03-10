package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Error extends Throwable {

    @Getter
    private final int errorCode;
    private final String errorMessage;
}
