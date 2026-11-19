ALTER TABLE users ADD COLUMN balance numeric(12, 2) NOT NULL DEFAULT 0.00;

CREATE TABLE cash_accounts (
    id uuid PRIMARY KEY,
    balance numeric(12, 2) NOT NULL CHECK (balance >= 0),
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL
);

INSERT INTO cash_accounts (id, balance, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000001', 0.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

CREATE TABLE financial_transactions (
    id uuid PRIMARY KEY,
    type varchar(32) NOT NULL CHECK (type IN ('EXPENSE', 'PAYMENT', 'CASH_ADJUSTMENT')),
    amount numeric(12, 2) NOT NULL,
    user_id uuid REFERENCES users(id) ON DELETE CASCADE,
    acting_user_id uuid NOT NULL REFERENCES users(id),
    expense_type varchar(16) CHECK (expense_type IN ('BEANS', 'OTHER')),
    bean_name varchar(255),
    weight_grams int,
    note varchar(500),
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    CHECK (
        (type = 'EXPENSE' AND amount > 0 AND user_id IS NOT NULL AND expense_type IS NOT NULL
            AND ((expense_type = 'BEANS' AND bean_name IS NOT NULL AND length(trim(bean_name)) > 0
                    AND weight_grams BETWEEN 100 AND 1000)
                OR (expense_type = 'OTHER' AND bean_name IS NULL AND weight_grams IS NULL)))
        OR (type = 'PAYMENT' AND amount > 0 AND user_id IS NOT NULL AND expense_type IS NULL
            AND bean_name IS NULL AND weight_grams IS NULL AND note IS NULL)
        OR (type = 'CASH_ADJUSTMENT' AND user_id IS NULL AND expense_type IS NULL AND bean_name IS NULL
            AND weight_grams IS NULL AND note IS NULL)
    )
);

CREATE INDEX ix_financial_transactions_created_at ON financial_transactions (created_at, id);
