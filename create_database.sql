-- Création de la base de données
CREATE DATABASE pay_my_buddy;

-- Connexion à la base de données
\c pay_my_buddy;

-- Création de la table users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

-- Création de la table transactions
CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              sender_id BIGINT NOT NULL,
                              receiver_id BIGINT NOT NULL,
                              description VARCHAR(255),
                              amount DECIMAL(10, 2) NOT NULL,
                              CONSTRAINT fk_sender FOREIGN KEY (sender_id) REFERENCES users (id),
                              CONSTRAINT fk_receiver FOREIGN KEY (receiver_id) REFERENCES users (id)
);

-- Création de la table connections
CREATE TABLE connections (
                             id BIGSERIAL PRIMARY KEY,
                             user_id BIGINT NOT NULL,
                             connection_id BIGINT NOT NULL,
                             CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id),
                             CONSTRAINT fk_connection FOREIGN KEY (connection_id) REFERENCES users (id),
                             CONSTRAINT unique_user_connection UNIQUE (user_id, connection_id)
);