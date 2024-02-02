package org.example.controller.advice;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.bean.dto.ApiErrorResponse;
import org.example.bean.dto.ApiResponse;
import org.example.exception.ApiException;
import org.example.exception.BusinessException;
import org.example.exception.SysCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException exception) {
        log.error("handleBusinessException", exception);
        return new ResponseEntity<>(new ApiErrorResponse(exception.getErrorCode().name(), Collections.singletonList(exception.getMessage())), HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ApiResponse> handleApiException(ApiException exception) {
        log.error("handleApiException", exception);
        return new ResponseEntity<>(new ApiResponse(exception.getCode().getCode(), StringUtils.isBlank(exception.getMessage()) ? exception.getCode().getMessage() : exception.getMessage(), Optional.ofNullable(exception.getData()).orElse(new HashMap<String, String>())), exception.getHttpStatus());
    }

    @ExceptionHandler(Throwable.class)
    ResponseEntity<ApiResponse> handleThrowable(Throwable throwable) {
        log.error("handleThrowable", throwable);
        Map<String, String> data = Maps.newHashMap();
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        data.put("error", stringWriter.toString());
        return new ResponseEntity<>(new ApiResponse(SysCode.ERROR.getCode(), throwable.getMessage(), data), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}