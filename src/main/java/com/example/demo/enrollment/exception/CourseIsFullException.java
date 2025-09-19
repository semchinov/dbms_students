// src/main/java/com/example/demo/enrollment/exception/CourseIsFullException.java

package com.example.demo.enrollment.exception;

public class CourseIsFullException extends RuntimeException {
    public CourseIsFullException(String message) {
        super(message);
    }
}
