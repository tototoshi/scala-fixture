#!SetUp
INSERT INTO users(id, name) VALUES (1, 'user1');
#!TearDown
DELETE FROM users WHERE id = 1;