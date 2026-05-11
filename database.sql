CREATE DATABASE SmartShoppingDB;

USE SmartShoppingDB;

-- USERS
CREATE TABLE users (
                       id INT PRIMARY KEY IDENTITY,
                       name NVARCHAR(100),
                       email NVARCHAR(100) UNIQUE,
                       password NVARCHAR(255),
                       created_at DATETIME DEFAULT GETDATE()
);

-- CATEGORIES
CREATE TABLE categories (
                            id INT PRIMARY KEY IDENTITY,
                            name NVARCHAR(100)
);

-- PRODUCTS
CREATE TABLE products (
                          id INT PRIMARY KEY IDENTITY,
                          name NVARCHAR(100),
                          price FLOAT,
                          category_id INT,
                          FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- EXPENSES
CREATE TABLE expenses (
                          id INT PRIMARY KEY IDENTITY,
                          user_id INT,
                          product_id INT,
                          quantity INT,
                          total_price FLOAT,
                          expense_date DATETIME DEFAULT GETDATE(),
                          FOREIGN KEY (user_id) REFERENCES users(id),
                          FOREIGN KEY (product_id) REFERENCES products(id)
);

-- BUDGETS
CREATE TABLE budgets (
                         id INT PRIMARY KEY IDENTITY,
                         user_id INT UNIQUE,
                         total_budget FLOAT,
                         FOREIGN KEY (user_id) REFERENCES users(id)
);
INSERT INTO categories (name) VALUES
                                  ('Food'),
                                  ('Transport'),
                                  ('Shopping');

INSERT INTO products (name, price, category_id) VALUES
                                                    ('Pain', 2.5, 1),
                                                    ('Taxi', 20, 2),
                                                    ('T-shirt', 100, 3);

INSERT INTO users (name, email, password)
VALUES ('Test User', 'test@test.com', '1234');

INSERT INTO budgets (user_id, total_budget)
VALUES (1, 0);

SELECT * FROM users;

SELECT * FROM categories;

SELECT * FROM products;

SELECT * FROM budgets;

SELECT * FROM expenses;

INSERT INTO categories (name) VALUES
                                  ('Food'),
                                  ('Transport'),
                                  ('Shopping'),
                                  ('Health'),
                                  ('Entertainment'),
                                  ('Bills'),
                                  ('Education');

INSERT INTO users (name, email, password) VALUES
                                              ('Fode', 'fode@gmail.com', '1234'),
                                              ('Hatimi', 'hatimi@gmail.com', '1234'),
                                              ('Gamran', 'gamran@gmail.com', '1234');

-- Ajout de produits pour différentes catégories
INSERT INTO products (name, price, category_id) VALUES
-- Alimentation (ID 1)
('Lait', 7.5, 1),
('Oeufs (12)', 15.0, 1),
('Café', 45.0, 1),
-- Transport (ID 2)
('Carburant', 200.0, 2),
('Tramway', 6.0, 2),
-- Santé (ID 4)
('Vitamines', 120.0, 4),
('Consultation', 250.0, 4),
-- Divertissement (ID 5)
('Cinéma', 60.0, 5),
('Abonnement Musique', 50.0, 5),
-- Factures (ID 6)
('Internet', 250.0, 6),
('Électricité', 300.0, 6);

-- Mise à jour du budget pour l'utilisateur 1 (Test User)
UPDATE budgets SET total_budget = 1500.0 WHERE user_id = 1;

-- Ajout de budgets pour les nouveaux utilisateurs
INSERT INTO budgets (user_id, total_budget) VALUES
                                                (2, 5000.0), -- Fode
                                                (3, 3500.0), -- Hatimi
                                                (4, 2000.0); -- Gamran

-- Dépenses pour l'utilisateur Gamran (User ID 4)
INSERT INTO expenses (user_id, product_id, quantity, total_price, expense_date) VALUES
                                                                                    (4, 1, 2, 5.0, '20260425 09:00:00'),
                                                                                    (4, 5, 1, 45.0, '20260426 10:30:00'),
                                                                                    (4, 11, 1, 60.0, '20260428 20:00:00'),
                                                                                    (4, 13, 1, 250.0, '2026-04-30T11:00:00'); -- Ajout du prix (250.0) ici

SELECT u.name, SUM(e.total_price) as total_depense, b.total_budget
FROM users u
         JOIN expenses e ON u.id = e.user_id
         JOIN budgets b ON u.id = b.user_id
GROUP BY u.name, b.total_budget;

SELECT c.name as Categorie, SUM(e.total_price) as Total
FROM categories c
         JOIN products p ON c.id = p.category_id
         JOIN expenses e ON p.id = e.product_id
GROUP BY c.name;


select * from users;
