package com.example.demo.enrollment.exception;

public class StudentNotFoundException extends EntityNotFoundException {
    public StudentNotFoundException(String message) {
        super(message);
    }
}
