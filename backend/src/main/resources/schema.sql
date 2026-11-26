-- Synthetic lab data only. Recreated on every start.
DROP TABLE IF EXISTS invoices CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS products CASCADE;

CREATE TABLE products (
    id     BIGINT PRIMARY KEY,
    name   VARCHAR(100)   NOT NULL,
    price  NUMERIC(10, 2) NOT NULL
);

-- Module 1, Task 1: the table a UNION-based injection tries to read.
CREATE TABLE users (
    id            BIGINT PRIMARY KEY,
    username      VARCHAR(40)  NOT NULL UNIQUE,
    role          VARCHAR(20)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL
);

-- Module 3: user_id is the subject (sub) of the caller's JWT.
CREATE TABLE profiles (
    user_id      VARCHAR(36) PRIMARY KEY,
    display_name VARCHAR(100),
    bio          VARCHAR(500)
);

-- Module 6
CREATE TABLE customers (
    id      VARCHAR(20) PRIMARY KEY,
    name    VARCHAR(100) NOT NULL,
    email   VARCHAR(254) NOT NULL,
    address VARCHAR(200)
);

-- Module 1.4 / reference access-control example
CREATE TABLE invoices (
    id          BIGINT PRIMARY KEY,
    owner_id    VARCHAR(36)    NOT NULL,
    amount      NUMERIC(10, 2) NOT NULL,
    description VARCHAR(200)
);
