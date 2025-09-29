-- Вставить admin, только если пользователя с таким username еще нет
INSERT INTO users (username, email, password, role)
SELECT 'admin', 'admin@example.com',
       '$2a$10$OAYifE1stJrY0SMhkmjPluhhpM7dAhQyDTHZ4vRUVcJOKJmrUnz7O',
       'ADMIN'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin'
);