package org.example.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SysCode implements ErrorCode {

    OK(1000, "Success"),
    FAIL(2000, "Expected error"),
    BAD_CONFIG(2300, "Misconfiguration"),

    NOT_FOUND(7000, "Data not found"),
    ERROR(9999, "Unexpected error");

    private final Integer code;
    private final String message;

}