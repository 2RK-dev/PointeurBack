UPDATE groups
SET type = 'CM'
WHERE type IS NULL;

UPDATE groups
SET classe = 'GB'
WHERE classe IS NULL;