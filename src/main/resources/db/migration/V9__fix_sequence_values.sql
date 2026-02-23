SELECT setval(pg_get_serial_sequence('groups', 'group_id'), MAX(group_id))
FROM groups;
SELECT setval(pg_get_serial_sequence('level', 'level_id'), MAX(level_id))
FROM level;
SELECT setval(pg_get_serial_sequence('room', 'room_id'), MAX(room_id))
FROM room;
SELECT setval(pg_get_serial_sequence('teacher', 'teacher_id'), MAX(teacher_id))
FROM teacher;
SELECT setval(pg_get_serial_sequence('teaching_unit', 'teaching_unit_id'), MAX(teaching_unit_id))
FROM teaching_unit;