package com.mistysoft.proceedhub.apps.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = UserController.class)
public class UserErrorHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> invalidUser(IllegalArgumentException exception) {
        HttpStatus status = exception.getMessage().contains("already exists")
                ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(exception.getMessage());
    }
}
