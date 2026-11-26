-- All names, e-mails and values below are fictional.
INSERT INTO products (id, name, price) VALUES
  (1, 'Desk lamp', 39.99),
  (2, 'Office chair', 249.00),
  (3, 'Standing desk', 599.00),
  (4, 'USB-C dock', 189.50),
  (5, 'Monitor arm', 129.00),
  (6, 'O''Neil notebook', 12.75);

-- The hashes are fake placeholders, not real password hashes.
INSERT INTO users (id, username, role, password_hash) VALUES
  (1, 'alice',   'CUSTOMER', '$2a$10$LAB.ONLY.FAKE.HASH.alice.000000000000000000000000'),
  (2, 'bob',     'CUSTOMER', '$2a$10$LAB.ONLY.FAKE.HASH.bob.00000000000000000000000000'),
  (3, 'support', 'SUPPORT',  '$2a$10$LAB.ONLY.FAKE.HASH.support.0000000000000000000000'),
  (4, 'admin',   'ADMIN',    '$2a$10$LAB.ONLY.FAKE.HASH.admin.000000000000000000000000');

-- user_id values match the user ids in keycloak/realm-labs.json
INSERT INTO profiles (user_id, display_name, bio) VALUES
  ('a11ce000-0000-4000-8000-000000000001', 'Alice', 'Customer since 2021'),
  ('b0b00000-0000-4000-8000-000000000002', 'Bob',   'Customer since 2023');

INSERT INTO customers (id, name, email, address) VALUES
  ('C-1001', 'Jane Doe',   'jane.doe@example.com',   '100 Example Street, Toronto, ON'),
  ('C-1002', 'Sam Rivera', 'sam.rivera@example.com', '200 Sample Avenue, Calgary, AB');

INSERT INTO invoices (id, owner_id, amount, description) VALUES
  (41, 'a11ce000-0000-4000-8000-000000000001', 120.00, 'Alice - October services'),
  (42, 'b0b00000-0000-4000-8000-000000000002', 980.00, 'Bob - October services');
