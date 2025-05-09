-- Drop existing tables if they exist
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;

-- Create users table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL
);

-- Create transactions table
CREATE TABLE transactions (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              amount DECIMAL(10, 2) NOT NULL,
                              description VARCHAR(255),
                              date DATE NOT NULL,
                              user_id BIGINT,
                              FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE transactions
  ADD COLUMN category VARCHAR(255) DEFAULT 'Misc.' NOT NULL;
