-- src/main/resources/set-max-students.sql

-- Установим конкретное значение для одного из курсов для теста
UPDATE courses SET max_students = 1 WHERE id = 1;
