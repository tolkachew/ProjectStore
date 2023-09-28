INSERT INTO firm (name, deleted) VALUES 
('Philips', false),
('Bosh', false),
('Mulinex', false);

INSERT INTO goods (name, firm_id, model, tech_characteristics, cost, guarantee_term, deleted) VALUES 
('Утюг', 1, 'model1', 'Мощность: 1400 Вт; Паровой удар до 60 г, Антипригарное покрытие подошвы', 9000, 30, false),
('Утюг', 2, 'model2', 'Мощность: 1800 Вт; длина шнура: 1,8 м, Объём резервуара для воды: 220 мл', 8158, 35, false),
('Электрочайник', 2, 'model3', 'Мощность: 2400 Вт; объём: 1,5 л, цвет: чёрный', 26682, 16, false),
('Электрочайник', 3, 'model4', 'Мощность: 2200 Вт; объём: 1,7 л, Нагревательный элемент: закрытая спираль', 20000, 20, false),
('Кухонный комбайн', 1, 'model5', 'Мощность: 500 Вт; объём: 2 л, количество скоростей: 2', 43400, 21, false),
('Кухонный комбайн', 3, 'model6', 'Мощность: 1000 Вт; объём: 3 л, количество скоростей: 2', 74900, 30, false);

INSERT INTO internet_markets (internet_address, is_delivery_paid, deleted) VALUES
('https://internetshop1.com', true, false),
('https://internetshop2.com', false, false),
('https://internetshop3.com', false, false);

INSERT INTO goods_offers (market_id, goods_id) VALUES
(1, 1),
(1, 2),
(2, 2),
(2, 3),
(3, 3),
(3, 1);

INSERT INTO orders (market_id, goods_id, order_date_time, amount, client_full_name, telephone, order_confirmation, deleted) VALUES
(1, 1, '2022-12-10 19:01:00', 2, 'Иванов Иван Иванович', '+375291234567', true, false),
(1, 2, '2022-12-11 10:00:00', 1, 'Петров Иван Иванович', '+375291111111', false, false),
(2, 2, '2022-12-12 10:00:00', 1, 'Роскольников Родион Иванович', '+375292222222', true, false),
(3, 3, '2022-12-12 11:00:00', 1, 'Безухов Пьер Иванович', '+3752933333333', false, false),
(3, 3, '2022-12-12 12:30:00', 1, 'Джобс Стив Иванович', '+375294444444', false, false);

INSERT INTO delivery (order_id, delivery_date_time, address, courier_full_name, delivery_cost, deleted) VALUES
(1, '2022-12-20 17:30:00', 'г. Витебск, ул. Пушкина, д. 1', 'Почтальон Печкин', 1000, false),
(3, '2022-12-21 17:30:00', 'г. Витебск, ул. Пушкина, д. 2', 'Курьерный Курьер Курьерович', 1000, false);