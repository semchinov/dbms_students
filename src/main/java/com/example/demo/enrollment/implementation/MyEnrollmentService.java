package com.example.demo.enrollment.implementation;

import com.example.demo.enrollment.EnrollmentService;
import com.example.demo.enrollment.exception.CourseIsFullException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MyEnrollmentService implements EnrollmentService {
    private final JdbcTemplate jdbcTemplate;

    public MyEnrollmentService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void enrollStudent(int studentId, int courseId) {
        boolean studentExists = Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(
                        "SELECT EXISTS (SELECT 1 FROM students WHERE id = ?)",
                        Boolean.class,
                        studentId
                )
        );
        if (!studentExists) {
            throw new IllegalArgumentException("Student with id " + studentId + " does not exist");
        }

        boolean courseExists = Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(
                        "SELECT EXISTS (SELECT 1 FROM courses WHERE id = ?)",
                        Boolean.class,
                        courseId
                )
        );
        if (!courseExists) {
            throw new IllegalArgumentException("Course with id " + courseId + " does not exist");
        }

        Integer maxStudents = jdbcTemplate.queryForObject(
                "SELECT max_students FROM courses WHERE id = ?",
                Integer.class,
                courseId
        );

        Integer currentCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM enrollments WHERE course_id = ?",
                Integer.class,
                courseId
        );

        if (maxStudents != null && maxStudents > 0 && currentCount != null && currentCount >= maxStudents) {
            throw new CourseIsFullException("Course with id " + courseId + " is full");
        }

        boolean alreadyEnrolled = Boolean.TRUE.equals(
                jdbcTemplate.queryForObject(
                        "SELECT EXISTS (SELECT 1 FROM enrollments WHERE student_id = ? AND course_id = ?)",
                        Boolean.class,
                        studentId,
                        courseId
                )
        );
        if (alreadyEnrolled) {
            return;
        }

        try {
            jdbcTemplate.update(
                    "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)",
                    studentId,
                    courseId
            );
        } catch (DuplicateKeyException ex) {
            // Student was already enrolled concurrently; ignore duplicate insert.
        }
    }
}
