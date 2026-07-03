package com.phuoclv.ai.common;

import com.phuoclv.ai.resource.IgnoreDecoration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import tools.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class ResponseDecorator implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper;

    public ResponseDecorator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.hasMethodAnnotation(IgnoreDecoration.class) &&
                !returnType.getParameterType().equals(ApiResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String stringBody) {
            try {
                return objectMapper.writeValueAsString(ApiResponse.success(stringBody));
            } catch (Exception e) {
                throw new RuntimeException("Error decorating raw String response", e);
            }
        }
        return ApiResponse.success(body);
    }
}
