ALTER TABLE users
ADD COLUMN user_type VARCHAR(20);

UPDATE users
SET user_type = 'USER'
WHERE user_type IS NULL;

ALTER TABLE users
ALTER COLUMN user_type SET NOT NULL;