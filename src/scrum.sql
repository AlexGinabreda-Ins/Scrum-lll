PRAGMA foreign_keys = ON;

BEGIN TRANSACTION;

-- Limpia primero las tablas dependientes para evitar datos antiguos o duplicados.
DELETE FROM ticket_products;
DELETE FROM ticket;
DELETE FROM user_supermarkets;
DELETE FROM products;
DELETE FROM supermarket;
DELETE FROM "user";

-- Reinicia autoincrementos de SQLite.
DELETE FROM sqlite_sequence WHERE name IN ('products', 'supermarket');

-- 10 usuarios random
INSERT INTO "user" (dni, name) VALUES
('18473926A', 'Nora Vidal'),
('59284617B', 'Hugo Serrano'),
('73829164C', 'Irene Molina'),
('42618593D', 'Lucas Ortega'),
('91537284E', 'Clara Navarro'),
('26749185F', 'Mario Rivas'),
('80351672G', 'Vera Castillo'),
('34962851H', 'Diego Ferrer'),
('67194528J', 'Sofia Campos'),
('52073816K', 'Adrian Lozano');

-- Supermercados
INSERT INTO supermarket (id, name, location) VALUES
(1, 'Mercadona', 'Barcelona'),
(2, 'Bonpreu', 'Badalona'),
(3, 'Lidl', 'Sant Cugat'),
(4, 'Carrefour', 'Hospitalet de Llobregat'),
(5, 'Aldi', 'Mataro'),
(6, 'Dia', 'Sabadell'),
(7, 'Consum', 'Terrassa'),
(8, 'Caprabo', 'Barcelona');

-- Relacion usuarios-supermercados
INSERT INTO user_supermarkets (user_dni, supermarket_id) VALUES
('18473926A', 1), ('18473926A', 2), ('18473926A', 8),
('59284617B', 3), ('59284617B', 4),
('73829164C', 1), ('73829164C', 5),
('42618593D', 2), ('42618593D', 6),
('91537284E', 4), ('91537284E', 7),
('26749185F', 3), ('26749185F', 8),
('80351672G', 5), ('80351672G', 6),
('34962851H', 1), ('34962851H', 7),
('67194528J', 2), ('67194528J', 4),
('52073816K', 6), ('52073816K', 8);

-- Productos
INSERT INTO products (id, name, price, quantity, type_product) VALUES
(1, 'Leche semidesnatada', 1.19, 1, 'Lacteos'),
(2, 'Pan integral', 1.75, 1, 'Panaderia'),
(3, 'Huevos docena', 2.89, 1, 'Frescos'),
(4, 'Arroz largo', 1.45, 1, 'Despensa'),
(5, 'Macarrones', 1.20, 1, 'Despensa'),
(6, 'Pechuga de pollo', 6.95, 1, 'Carne'),
(7, 'Merluza congelada', 7.50, 1, 'Pescado'),
(8, 'Manzanas golden', 2.25, 1, 'Fruta'),
(9, 'Platanos', 1.80, 1, 'Fruta'),
(10, 'Lechuga romana', 1.10, 1, 'Verdura'),
(11, 'Tomates cherry', 2.40, 1, 'Verdura'),
(12, 'Garbanzos cocidos', 1.35, 1, 'Legumbres'),
(13, 'Lentejas cocidas', 1.30, 1, 'Legumbres'),
(14, 'Aceite de oliva', 8.99, 1, 'Despensa'),
(15, 'Yogures naturales', 2.20, 1, 'Lacteos'),
(16, 'Queso rallado', 2.95, 1, 'Lacteos'),
(17, 'Cereales de avena', 3.10, 1, 'Desayuno'),
(18, 'Cafe molido', 3.75, 1, 'Desayuno'),
(19, 'Agua mineral pack', 3.20, 1, 'Bebidas'),
(20, 'Zumo de naranja', 1.95, 1, 'Bebidas'),
(21, 'Pizza fresca', 4.25, 1, 'Preparados'),
(22, 'Jamon cocido', 2.70, 1, 'Charcuteria'),
(23, 'Detergente liquido', 5.80, 1, 'Limpieza'),
(24, 'Papel higienico', 4.60, 1, 'Hogar'),
(25, 'Chocolate negro', 1.85, 1, 'Dulces');

-- Tickets
INSERT INTO ticket (id, date, user_dni, supermarket_id) VALUES
(2001, '02/05/2026', '18473926A', 1),
(2002, '03/05/2026', '59284617B', 3),
(2003, '04/05/2026', '73829164C', 5),
(2004, '05/05/2026', '42618593D', 2),
(2005, '06/05/2026', '91537284E', 4),
(2006, '07/05/2026', '26749185F', 8),
(2007, '08/05/2026', '80351672G', 6),
(2008, '09/05/2026', '34962851H', 7),
(2009, '10/05/2026', '67194528J', 2),
(2010, '11/05/2026', '52073816K', 8),
(2011, '12/05/2026', '18473926A', 8),
(2012, '13/05/2026', '73829164C', 1),
(2013, '14/05/2026', '91537284E', 7),
(2014, '15/05/2026', '26749185F', 3),
(2015, '16/05/2026', '80351672G', 5);

-- Productos de cada ticket
INSERT INTO ticket_products (ticket_id, product_id, quantity, price) VALUES
(2001, 1, 2, 1.19), (2001, 2, 1, 1.75), (2001, 3, 1, 2.89), (2001, 8, 2, 2.25),
(2002, 4, 2, 1.45), (2002, 5, 3, 1.20), (2002, 6, 1, 6.95), (2002, 15, 2, 2.20),
(2003, 7, 1, 7.50), (2003, 10, 1, 1.10), (2003, 12, 2, 1.35), (2003, 19, 1, 3.20),
(2004, 1, 1, 1.19), (2004, 14, 1, 8.99), (2004, 18, 1, 3.75), (2004, 25, 2, 1.85),
(2005, 6, 2, 6.95), (2005, 9, 6, 1.80), (2005, 11, 1, 2.40), (2005, 16, 1, 2.95),
(2006, 2, 2, 1.75), (2006, 17, 1, 3.10), (2006, 20, 2, 1.95), (2006, 24, 1, 4.60),
(2007, 3, 1, 2.89), (2007, 4, 1, 1.45), (2007, 13, 2, 1.30), (2007, 23, 1, 5.80),
(2008, 7, 2, 7.50), (2008, 8, 1, 2.25), (2008, 10, 2, 1.10), (2008, 21, 1, 4.25),
(2009, 5, 2, 1.20), (2009, 6, 1, 6.95), (2009, 12, 1, 1.35), (2009, 22, 1, 2.70),
(2010, 1, 2, 1.19), (2010, 15, 1, 2.20), (2010, 19, 2, 3.20), (2010, 25, 1, 1.85),
(2011, 11, 2, 2.40), (2011, 14, 1, 8.99), (2011, 18, 1, 3.75),
(2012, 4, 3, 1.45), (2012, 6, 1, 6.95), (2012, 9, 5, 1.80),
(2013, 3, 2, 2.89), (2013, 16, 1, 2.95), (2013, 20, 3, 1.95),
(2014, 5, 4, 1.20), (2014, 7, 1, 7.50), (2014, 17, 1, 3.10),
(2015, 2, 1, 1.75), (2015, 8, 3, 2.25), (2015, 13, 1, 1.30), (2015, 24, 1, 4.60);

COMMIT;
