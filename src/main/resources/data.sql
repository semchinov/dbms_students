-- Преподы, на которых ссылаются курсы
INSERT INTO teachers (id, full_name, email) VALUES
                                                (1, 'Ivan Petrov', 'ivan.petrov@example.com'),
                                                (2, 'Elena Smirnova', 'elena.smirnova@example.com'),
                                                (3, 'Dmitry Sokolov', 'dmitry.sokolov@example.com'),
                                                (4, 'Anna Volkova', 'anna.volkova@example.com'),
                                                (5, 'Sergey Ivanov', 'sergey.ivanov@example.com'),
                                                (6, 'Maria Petrova', 'maria.petrova@example.com');

-- Курсы с новым столбцом max_students (для первого курса ставим 1 место)
INSERT INTO courses (id, name, generation_hours, instructor_id, max_students) VALUES
                                                                                  (1, 'Database Fundamentals', 40, 1, 1),
                                                                                  (2, 'Advanced SQL', 60, 1, 20),
                                                                                  (3, 'Programming in Python', 80, 2, 20),
                                                                                  (4, 'Machine Learning Basics', 100, 3, 20),
                                                                                  (5, 'Algorithms and Data Structures', 70, 4, 20),
                                                                                  (6, 'Web Development', 50, 5, 20);

-- Студенты
INSERT INTO students (id, full_name, enrolled_at) VALUES
                                                      (1, 'Alexey Popov', '2025-01-15'),
                                                      (2, 'Olga Kuznetsova', '2025-02-01'),
                                                      (3, 'Mikhail Ivanov', '2025-02-20'),
                                                      (4, 'Natalia Romanova', '2025-03-05'),
                                                      (5, 'Pavel Smirnov', '2025-03-10'),
                                                      (6, 'Irina Petrova', '2025-09-01');

-- Записи на курсы и оценки
INSERT INTO enrollments (student_id, course_id, grade) VALUES
                                                           (1, 1, 85.5),
                                                           (1, 2, 90.0),
                                                           (2, 3, 75.0),
                                                           (3, 4, 88.5),
                                                           (4, 5, 92.0),
                                                           (5, 6, 70.0),
                                                           (6, 1, 95.0);

-- Подгоняем последовательности, чтобы следующие ID не конфликтовали с уже вставленными
SELECT setval(pg_get_serial_sequence('teachers', 'id'), (SELECT MAX(id) FROM teachers));
SELECT setval(pg_get_serial_sequence('courses', 'id'), (SELECT MAX(id) FROM courses));
SELECT setval(pg_get_serial_sequence('students', 'id'), (SELECT MAX(id) FROM students));
SELECT setval(pg_get_serial_sequence('departments', 'id'), COALESCE((SELECT MAX(id) FROM departments), 1));
SELECT setval(pg_get_serial_sequence('employees', 'id'), COALESCE((SELECT MAX(id) FROM employees), 1));
SELECT setval(pg_get_serial_sequence('tickets', 'id'), COALESCE((SELECT MAX(id) FROM tickets), 1));
