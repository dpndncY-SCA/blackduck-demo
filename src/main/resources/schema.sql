CREATE TABLE IF NOT EXISTS users (
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100),
    email VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS products (
    id    INT PRIMARY KEY AUTO_INCREMENT,
    name  VARCHAR(100),
    price DECIMAL(10, 2)
);

INSERT INTO users (name, email) VALUES ('Alice', 'alice@example.com');
INSERT INTO users (name, email) VALUES ('Bob', 'bob@example.com');
INSERT INTO users (name, email) VALUES ('Charlie', 'charlie@example.com');

INSERT INTO products (name, price) VALUES ('Widget A', 9.99);
INSERT INTO products (name, price) VALUES ('Widget B', 19.99);
INSERT INTO products (name, price) VALUES ('Widget C', 49.99);
