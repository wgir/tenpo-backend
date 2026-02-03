CREATE TABLE clients (
    client_id SERIAL PRIMARY KEY,
    client_name VARCHAR(255) NOT NULL,
    client_rut VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE employees (
    employee_id SERIAL PRIMARY KEY,
    employee_name VARCHAR(255) NOT NULL,
    employee_rut VARCHAR(255) NOT NULL UNIQUE,
    client_id INTEGER NOT NULL REFERENCES clients(client_id)
);

CREATE TABLE transactions (
    transaction_id SERIAL PRIMARY KEY,
    transaction_amount INTEGER NOT NULL,
    merchant_or_business VARCHAR(255) NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    employee_id INTEGER NOT NULL REFERENCES employees(employee_id)
);
