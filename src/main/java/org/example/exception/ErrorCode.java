package org.example.exception;

import java.io.Serializable;

public interface ErrorCode extends Serializable {

    String getMessage();

    Integer getCode();

    String name();

}