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

-- Insert users
INSERT INTO users (username, email, password) VALUES ('John', 'testuser1@example.com', 'john');
INSERT INTO users (username, email, password) VALUES ('Jane', 'testuser2@example.com', 'jane');
INSERT INTO users (username, email, password) VALUES ('Bob', 'testuser3@example.com', 'bob');

-- Insert transactions for testuser1
INSERT INTO transactions (amount, description, date, user_id) VALUES (100.00, 'Salary', '2024-10-01', 1);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-50.00, 'Groceries', '2024-10-05', 1);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-20.00, 'Transport', '2024-10-10', 1);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-10.00, 'Coffee', '2024-10-15', 1);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-5.00, 'Snacks', '2024-10-20', 1);

-- Insert transactions for testuser2
INSERT INTO transactions (amount, description, date, user_id) VALUES (200.00, 'Freelance Work', '2024-10-03', 2);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-70.00, 'Utilities', '2024-10-08', 2);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-30.00, 'Dinner', '2024-10-13', 2);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-15.00, 'Movies', '2024-10-18', 2);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-25.00, 'Books', '2024-10-23', 2);

-- Insert transactions for testuser3
INSERT INTO transactions (amount, description, date, user_id) VALUES (150.00, 'Bonus', '2024-10-05', 3);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-60.00, 'Clothing', '2024-10-10', 3);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-40.00, 'Electronics', '2024-10-15', 3);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-10.00, 'Coffee', '2024-10-20', 3);
INSERT INTO transactions (amount, description, date, user_id) VALUES (-20.00, 'Gym', '2024-10-25', 3);


