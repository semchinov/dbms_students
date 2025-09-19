// src/main/java/com/example/demo/enrollment/exception/CourseNotFoundException.java

package com.example.demo.enrollment.exception;

public class CourseNotFoundException extends EntityNotFoundException {
    public CourseNotFoundException(String message) {
        super(message);
    }
}
