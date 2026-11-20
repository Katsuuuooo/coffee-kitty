SET TIME ZONE 'UTC';

CREATE TABLE coffee_corrections (
    id uuid NOT NULL PRIMARY KEY,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    user_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    count_before int NOT NULL CHECK (count_before >= 0),
    count_after int NOT NULL CHECK (count_after >= 0),
    note text NOT NULL,
    admin_id uuid NOT NULL REFERENCES users(id) ON DELETE CASCADE
);
