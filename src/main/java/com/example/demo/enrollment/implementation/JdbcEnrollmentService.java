//src/main/java/com/example/demo/enrollment/implementation/JdbcEnrollmentService.java

package com.example.demo.enrollment.implementation;

import com.example.demo.enrollment.EnrollmentService;
import com.example.demo.enrollment.exception.CourseIsFullException;
import com.example.demo.enrollment.exception.CourseNotFoundException;
import com.example.demo.enrollment.exception.StudentNotFoundException;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class JdbcEnrollmentService implements EnrollmentService {
    private final DataSource dataSource;

    public JdbcEnrollmentService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void enrollStudent(int studentId, int courseId) {
        safeEnrollStudent(studentId, courseId);
    }

    public void safeEnrollStudent(int studentId, int courseId) {
        Connection connection = null;
        boolean previousAutoCommit = true;
        try {
            connection = dataSource.getConnection();
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            ensureStudentExists(connection, studentId);
            ensureCourseExists(connection, courseId);

            Integer maxStudents = findMaxStudentsForUpdate(connection, courseId);
            int currentCount = countEnrollments(connection, courseId);

            if (maxStudents != null && maxStudents > 0 && currentCount >= maxStudents) {
                throw new CourseIsFullException("Course with id " + courseId + " is full");
            }

            if (isAlreadyEnrolled(connection, studentId, courseId)) {
                connection.commit();
                return;
            }

            insertEnrollment(connection, studentId, courseId);

            connection.commit();
        } catch (RuntimeException ex) {
            rollbackQuietly(connection);
            throw ex;
        } catch (SQLException ex) {
            rollbackQuietly(connection);
            throw new RuntimeException("Failed to enroll student " + studentId + " into course " + courseId, ex);
        } finally {
            resetAutoCommitQuietly(connection, previousAutoCommit);
            closeQuietly(connection);
        }
    }

    private void ensureStudentExists(Connection connection, int studentId) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM students WHERE id = ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next() || !resultSet.getBoolean(1)) {
                    throw new StudentNotFoundException("Student with id " + studentId + " does not exist");
                }
            }
        }
    }

    private void ensureCourseExists(Connection connection, int courseId) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM courses WHERE id = ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next() || !resultSet.getBoolean(1)) {
                    throw new CourseNotFoundException("Course with id " + courseId + " does not exist");
                }
            }
        }
    }

    private Integer findMaxStudentsForUpdate(Connection connection, int courseId) throws SQLException {
        String sql = "SELECT max_students FROM courses WHERE id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int maxStudents = resultSet.getInt(1);
                    if (resultSet.wasNull()) {
                        return null;
                    }
                    return maxStudents;
                }
            }
        }
        throw new CourseNotFoundException("Course with id " + courseId + " does not exist");
    }

    private int countEnrollments(Connection connection, int courseId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE course_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

    private boolean isAlreadyEnrolled(Connection connection, int studentId, int courseId) throws SQLException {
        String sql = "SELECT EXISTS (SELECT 1 FROM enrollments WHERE student_id = ? AND course_id = ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() && resultSet.getBoolean(1);
            }
        }
    }

    private void insertEnrollment(Connection connection, int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);
            statement.executeUpdate();
        }
    }

    private void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private void resetAutoCommitQuietly(Connection connection, boolean autoCommit) {
        if (connection != null) {
            try {
                connection.setAutoCommit(autoCommit);
            } catch (SQLException ignored) {
            }
        }
    }

    private void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
