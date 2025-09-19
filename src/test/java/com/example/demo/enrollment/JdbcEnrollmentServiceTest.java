// src/test/java/com/example/demo/enrollment/JdbcEnrollmentServiceTest.java
package com.example.demo.enrollment;

import com.example.demo.enrollment.exception.CourseIsFullException;
import com.example.demo.enrollment.implementation.JdbcEnrollmentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class JdbcEnrollmentServiceTest {

    private static final int COURSE_ID = 2;
    private static final int FIRST_STUDENT_ID = 1;
    private static final int SECOND_STUDENT_ID = 2;

    @Autowired
    private JdbcEnrollmentService jdbcEnrollmentService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Integer originalMaxStudents;

    @BeforeEach
    void setUp() {
        originalMaxStudents = jdbcTemplate.queryForObject(
                "SELECT max_students FROM courses WHERE id = ?",
                Integer.class,
                COURSE_ID
        );

        jdbcTemplate.update("DELETE FROM enrollments WHERE course_id = ?", COURSE_ID);
        jdbcTemplate.update("UPDATE courses SET max_students = 1 WHERE id = ?", COURSE_ID);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM enrollments WHERE course_id = ?", COURSE_ID);
        jdbcTemplate.update(
                "UPDATE courses SET max_students = ? WHERE id = ?",
                originalMaxStudents,
                COURSE_ID
        );
    }

    @Test
    void safeEnrollStudent_insertsNewEnrollmentWhenCapacityAllows() {
        jdbcEnrollmentService.safeEnrollStudent(FIRST_STUDENT_ID, COURSE_ID);

        int enrollmentCount = Objects.requireNonNull(
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM enrollments WHERE course_id = ?",
                        Integer.class,
                        COURSE_ID
                )
        );

        assertThat(enrollmentCount).isEqualTo(1);
    }

    @Test
    void safeEnrollStudent_throwsWhenCourseIsFull() {
        jdbcTemplate.update(
                "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)",
                FIRST_STUDENT_ID,
                COURSE_ID
        );

        assertThrows(
                CourseIsFullException.class,
                () -> jdbcEnrollmentService.safeEnrollStudent(SECOND_STUDENT_ID, COURSE_ID)
        );

        int enrollmentCount = Objects.requireNonNull(
                jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM enrollments WHERE course_id = ?",
                        Integer.class,
                        COURSE_ID
                )
        );

        assertThat(enrollmentCount).isEqualTo(1);
    }
}
