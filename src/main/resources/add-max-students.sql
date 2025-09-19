-- src/main/resources/add-max-students.sql

ALTER TABLE courses ADD COLUMN IF NOT EXISTS max_students INT DEFAULT 20;
-- Установим конкретное значение для одного из курсов для теста
UPDATE courses SET max_students = 1 WHERE id = 1;
