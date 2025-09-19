package com.example.demo.enrollment.exception;

public class CourseNotFoundException extends EntityNotFoundException {
    public CourseNotFoundException(String message) {
        super(message);
    }
}
