// src/main/java/com/example/demo/enrollment/exception/EntityNotFoundException.java

package com.example.demo.enrollment.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
