-- Insérer des utilisateurs dans la table users
INSERT INTO users (id, username, email, password)
VALUES (1, 'user1', 'user1@example.com', 'password1'),
       (2, 'user2', 'user2@example.com', 'password2'),
       (3, 'user3', 'user3@example.com', 'password3');

-- Insérer des transactions dans la table transactions
INSERT INTO transactions (id, sender_id, receiver_id, description, amount)
VALUES (1, 1, 2, 'Payment for lunch', 25.50),
       (2, 2, 3, 'Gift', 50.00),
       (3, 3, 1, 'Refund', 15.75);

-- Insérer des connexions dans la table connections
INSERT INTO connections (id, user_id, connection_id)
VALUES (1, 1, 2),
       (2, 2, 3),
       (3, 3, 1);