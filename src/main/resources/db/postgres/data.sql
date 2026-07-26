INSERT INTO customers (id, name, email) VALUES (1, 'John Doe', 'john.doe@mail.com') ON CONFLICT (id) DO NOTHING;
