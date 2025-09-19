package com.example.demo.enrollment.exception;

public class CourseIsFullException extends RuntimeException {
    public CourseIsFullException(String message) {
        super(message);
    }
}
