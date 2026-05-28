-- Przykładowe dane

INSERT INTO club(name, city_id) VALUES
('Szachiści z Opola', 11),
('Skoczek', NULL);

INSERT INTO player(name, surname, rating, club_id) VALUES
('Magnus', 'Carlsen', 2840, 1),
('Hikaru', 'Nakamura', 2792, 1),
('Fabiano', 'Caruana', 2788, NULL),
('Nodirbek', 'Abdusattorov', 2780, 2),
('Javokhir', 'Sindarov', 2770, NULL),
('Anish', 'Giri', 2767, NULL),
('Alireza', 'Firouzja', 2759, 2),
('Vincent', 'Keymer', 2759, 2),
('Wesley', 'So', 2754, NULL),
('Wei', 'Yi', 2753, 1),
('Beata', 'Andrejczuk', 1500, NULL),
('Maciej', 'Adamski', 1400, NULL);

UPDATE club SET president=1 WHERE club_id=1;
UPDATE club SET president=4 WHERE club_id=2;

INSERT INTO arbiter_class_history(arbiter_id, arbiter_class_id, date_since) VALUES
(11, 3, '2004-04-05'),
(11, 5, '2006-05-13'),
(11, 6, '2012-07-12'),
(12, 2, '2023-06-24');

INSERT INTO tournament(name, tempo_id, system_id, time_start, time_end, city_id, address, organiser, main_arbiter) VALUES
('Mistrzostwa Białystoku', 1, 3, '2024-06-12 11:00:00+02', '2024-06-14 20:00:00+02', 1, 'Sienkiewicza 55a lok. 70', 11, 11),
('3. Lubuski Konkurs szachowy', 4, 1, '2026-05-02 12:00:00+02', '2026-05-05 16:00:00+02', 4, 'Chrobrego 28', 1, 12);

INSERT INTO tournament_arbiter(tournament_id, arbiter_id) VALUES
(1, 11),
(2, 11),
(2, 12);

INSERT INTO tournament_player(tournament_id, player_id, score) VALUES
(1, 1, 2),
(1, 2, 1),
(1, 3, 0),
(1, 4, 0),
(2, 3, 0.5),
(2, 4, 0.5),
(2, 5, 0),
(2, 7, 0);

INSERT INTO round(time_start, time_end, tournament_id) VALUES
('2024-06-13 11:30:00+02', '2024-06-13 19:30:00+02', 1),
('2024-06-14 11:30:00+02', '2024-06-14 19:30:00+02', 1),
('2026-05-02 13:00:00+02', '2026-05-02 13:30:00+02', 2);

INSERT INTO game(round_id, white, black) VALUES
(1, 1, 3), (1, 2, 4), (2, 1, 2), (3, 3, 4), (3, 5, 7);

INSERT INTO game_over(round_id, white, white_score, black_score, game_over_reason_id, arbiter_id) VALUES
(1, 1, 1, 0, 2, 11), (1, 2, 1, 0, 1, 11), (2, 1, 1, 0, 1, 11), (3, 3, 0.5, 0.5, 5, 12);

INSERT INTO round_rating(round_id, player_id, rating_change) VALUES
(1, 1, 3), (1, 2, 2), (1, 3, -1), (1, 4, -2), (2, 1, 3), (2, 2, -1), (3, 3, 0), (3, 4, 0);

INSERT INTO title_history(player_id, title_id, tournament_id) VALUES
(1, 1, 1), (1, 5, 2);

INSERT INTO player_class_history(player_id, tournament_id, player_class_id) VALUES
(1, 1, 5), (3, 1, 2);

INSERT INTO penalty(player_id, date_since, date_until, reason, tournament_id, arbiter_id) VALUES
(4, '2024-06-13', '2027-06-13', 'Rażące naruszenie regulaminu', 1, 12);

INSERT INTO rating_history(player_id, tournament_id, rating) VALUES
(1, 1, 2840), (2, 1, 2792), (3, 1, 2788), (4, 1, 2780);

INSERT INTO norm(player_id, tournament_id, title_id, date_until) VALUES
    (3, 1, 9, '2025-06-13'), (4, 2, 5, '2027-05-02');
