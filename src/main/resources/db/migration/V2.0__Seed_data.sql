INSERT INTO users (id, email, password_hash, first_name, last_name, phone, google_id, role, status)
VALUES
(1, 'admin@cobanking.com', '$2a$10$7QJx8xQwLxT4b9C8x9D2deZPzVfYc9b3g5S7f5bC1nUuQm3XQnW6G', 'Admin', 'User', '0712345678', NULL, 'ADMIN', 'ACTIVE'),
(2, 'user@cobanking.com', '$2a$10$7QJx8xQwLxT4b9C8x9D2deZPzVfYc9b3g5S7f5bC1nUuQm3XQnW6G', 'Demo', 'User', '0711111111', NULL, 'USER', 'ACTIVE');

INSERT INTO accounts (id, user_id, account_number, account_type, balance, currency, status)
VALUES
(1, 2, '1000000001', 'CHECKING', 5000.00, 'USD', 'ACTIVE'),
(2, 2, '1000000002', 'SAVINGS', 12000.00, 'USD', 'ACTIVE'),
(3, 1, '9999999999', 'CHECKING', 25000.00, 'USD', 'ACTIVE');

INSERT INTO bills (id, user_id, account_id, payee_name, amount, due_date, status, description)
VALUES
(1, 2, 1, 'Electric Company', 125.50, DATE_ADD(CURRENT_DATE, INTERVAL 7 DAY), 'PENDING', 'Monthly utility bill'),
(2, 2, 2, 'Water Board', 65.25, DATE_ADD(CURRENT_DATE, INTERVAL 10 DAY), 'PENDING', 'Monthly water bill');

INSERT INTO investments (id, user_id, account_id, symbol, shares, purchase_price, total_value, status, purchased_at)
VALUES
(1, 2, 2, 'AAPL', 10.0000, 150.00, 1500.00, 'ACTIVE', CURRENT_TIMESTAMP),
(2, 2, 2, 'MSFT', 5.0000, 320.00, 1600.00, 'ACTIVE', CURRENT_TIMESTAMP);

INSERT INTO notifications (id, user_id, title, message, notification_type, is_read)
VALUES
(1, 2, 'Welcome', 'Welcome to Co-Banking.', 'GENERAL', FALSE),
(2, 2, 'Bill Reminder', 'Your electric bill is due soon.', 'BILL', FALSE);

