-- /src/main/resources/data.sql


-- Преподы, на которых ссылаются курсы
INSERT INTO teachers (id, full_name, email) VALUES
    (1, 'Ivan Petrov', 'ivan.petrov@example.com'),
    (2, 'Elena Smirnova', 'elena.smirnova@example.com'),
    (3, 'Dmitry Sokolov', 'dmitry.sokolov@example.com'),
    (4, 'Anna Volkova', 'anna.volkova@example.com'),
    (5, 'Sergey Ivanov', 'sergey.ivanov@example.com'),
    (6, 'Maria Petrova', 'maria.petrova@example.com');

-- Курсы
INSERT INTO courses (id, name, generation_hours, instructor_id) VALUES
    (1, 'Database Fundamentals', 40, 1),
    (2, 'Advanced SQL', 60, 1),
    (3, 'Programming in Python', 80, 2),
    (4, 'Machine Learning Basics', 100, 3),
    (5, 'Algorithms and Data Structures', 70, 4),
    (6, 'Web Development', 50, 5);

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
