package com.ll.medium.domain.post.post.controller.exceptionHandler;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ll.medium.domain.post.post.controller.ApiV1PostController;

@ControllerAdvice(basePackageClasses = ApiV1PostController.class)
public class PostExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        HashMap<String, String> data = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            data.put(fieldName, errorMessage);
        });

        return ResponseEntity.badRequest().body(data);
    }
}
