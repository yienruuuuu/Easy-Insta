package org.example.controller.advice;


import org.example.bean.dto.ApiResponse;
import org.example.exception.SysCode;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Optional;

@ControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, @NonNull Class converterType) {
        return methodParameter.getDeclaringClass().getPackage().getName().startsWith("org.example.controller");
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter methodParameter, @NonNull MediaType mediaType, @NonNull Class converterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body instanceof ApiResponse) {
            return body;
        }
        return new ApiResponse(SysCode.OK.getCode(), SysCode.OK.getMessage(), Optional.ofNullable(body).orElseGet(HashMap::new));
    }

}