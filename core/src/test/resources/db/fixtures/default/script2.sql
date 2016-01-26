#!SetUp
INSERT INTO users(id, name) VALUES (2, 'user2');
#!TearDown
DELETE FROM users where id = 2;