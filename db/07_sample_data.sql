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

-- Czołowi arcymistrzowie świata (ranking FIDE, wartości orientacyjne; pierwsza 10-tka jest już wyżej)
INSERT INTO player(name, surname, rating, club_id) VALUES
('Arjun', 'Erigaisi', 2799, NULL),
('Gukesh', 'Dommaraju', 2787, NULL),
('Praggnanandhaa', 'Rameshbabu', 2763, NULL),
('Ian', 'Nepomniachtchi', 2758, NULL),
('Ding', 'Liren', 2755, NULL),
('Viswanathan', 'Anand', 2751, NULL),
('Shakhriyar', 'Mamedyarov', 2748, NULL),
('Yu', 'Yangyi', 2744, NULL),
('Leinier', 'Dominguez', 2742, NULL),
('Levon', 'Aronian', 2740, NULL),
('Vladislav', 'Artemiev', 2737, NULL),
('Alexander', 'Grischuk', 2734, NULL),
('Wang', 'Hao', 2731, NULL),
('Maxime', 'Vachier-Lagrave', 2729, NULL),
('Jan-Krzysztof', 'Duda', 2727, NULL),
('Sergey', 'Karjakin', 2724, NULL),
('Le', 'Quang Liem', 2722, NULL),
('Pentala', 'Harikrishna', 2719, NULL),
('Vidit', 'Gujrathi', 2716, NULL),
('Richard', 'Rapport', 2714, NULL),
('Parham', 'Maghsoodloo', 2712, NULL),
('Dmitry', 'Andreikin', 2710, NULL),
('Teimour', 'Radjabov', 2708, NULL),
('Daniil', 'Dubov', 2706, NULL),
('Andrey', 'Esipenko', 2704, NULL),
('Nikita', 'Vitiugov', 2702, NULL),
('Vladimir', 'Fedoseev', 2700, NULL),
('Amin', 'Tabatabaei', 2698, NULL),
('Bu', 'Xiangzhi', 2696, NULL),
('Sam', 'Shankland', 2694, NULL),
('Aravindh', 'Chithambaram', 2692, NULL),
('Nihal', 'Sarin', 2690, NULL),
('David', 'Navara', 2688, NULL),
('Peter', 'Svidler', 2686, NULL),
('Radoslaw', 'Wojtaszek', 2684, NULL),
('Bogdan-Daniel', 'Deac', 2682, NULL),
('Etienne', 'Bacrot', 2680, NULL),
('Boris', 'Gelfand', 2678, NULL),
('Hans', 'Niemann', 2676, NULL),
('Jorden', 'Van Foreest', 2674, NULL),
('Nodirbek', 'Yakubboev', 2672, NULL),
('Kirill', 'Shevchenko', 2670, NULL),
('Matthias', 'Bluebaum', 2668, NULL),
('Dmitrij', 'Kollars', 2666, NULL),
('Gabriel', 'Sargissian', 2664, NULL),
('Rauf', 'Mamedov', 2662, NULL),
('Salem', 'Saleh', 2660, NULL),
('Bassem', 'Amin', 2658, NULL),
('Anton', 'Korobov', 2656, NULL),
('Maxim', 'Matlakov', 2654, NULL),
('Evgeniy', 'Najer', 2652, NULL),
('Ivan', 'Cheparinov', 2650, NULL),
('Markus', 'Ragger', 2648, NULL),
('Baadur', 'Jobava', 2646, NULL),
('Ivan', 'Saric', 2644, NULL),
('Francisco', 'Vallejo', 2642, NULL),
('David', 'Anton', 2640, NULL),
('Nils', 'Grandelius', 2638, NULL),
('Gawain', 'Jones', 2636, NULL),
('Benjamin', 'Bok', 2634, NULL),
('Kirill', 'Alekseenko', 2632, NULL),
('Velimir', 'Ivic', 2630, NULL),
('Raunak', 'Sadhwani', 2628, NULL),
('Volodar', 'Murzin', 2626, NULL),
('Frederik', 'Svane', 2624, NULL),
('Daniel', 'Dardha', 2622, NULL),
('Sam', 'Sevian', 2620, NULL),
('Jeffery', 'Xiong', 2618, NULL),
('Awonder', 'Liang', 2616, NULL),
('Daniel', 'Naroditsky', 2614, NULL),
('Ray', 'Robson', 2612, NULL),
('Pavel', 'Eljanov', 2610, NULL),
('Anton', 'Demchenko', 2608, NULL),
('Maxim', 'Rodshtein', 2606, NULL),
('Karthikeyan', 'Murali', 2604, NULL),
('Aryan', 'Tari', 2602, NULL),
('Krishnan', 'Sasikiran', 2600, NULL),
('Surya', 'Ganguly', 2598, NULL),
('Baskaran', 'Adhiban', 2596, NULL),
('Marc''Andria', 'Maurizzi', 2594, NULL),
('Abhimanyu', 'Mishra', 2592, NULL),
('Mateusz', 'Bartel', 2590, NULL),
('Kacper', 'Piorun', 2588, NULL),
('Kamil', 'Dragun', 2586, NULL),
('Pawel', 'Teclaf', 2584, NULL),
('Grzegorz', 'Gajewski', 2582, NULL),
('Jacek', 'Tomczak', 2580, NULL),
('Klementy', 'Sychev', 2578, NULL),
('Daniil', 'Yuffa', 2576, NULL),
('Sanan', 'Sjugirov', 2574, NULL);

UPDATE club SET president=1 WHERE club_id=1;
UPDATE club SET president=4 WHERE club_id=2;

INSERT INTO arbiter_class_history(arbiter_id, arbiter_class_id, date_since) VALUES
(11, 3, '2004-04-05'),
(11, 5, '2006-05-13'),
(11, 6, '2012-07-12'),
(12, 2, '2023-06-24');

INSERT INTO tournament(name, tempo_id, system_id, number_of_rounds, time_start, time_end, city_id, address, organiser, main_arbiter) VALUES
('Mistrzostwa Białystoku', 1, 3, 9, '2024-06-12 11:00:00+02', '2024-06-14 20:00:00+02', 1, 'Sienkiewicza 55a lok. 70', 11, 11),
('3. Lubuski Konkurs szachowy', 4, 1, 7, '2026-05-02 12:00:00+02', '2026-05-05 16:00:00+02', 4, 'Chrobrego 28', 1, 12);

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

INSERT INTO game_over(round_id, white, white_won, game_over_reason_id, arbiter_id) VALUES
(1, 1, TRUE, 2, 11), (1, 2, TRUE, 1, 11), (2, 1, TRUE, 1, 11), (3, 3, TRUE, 5, 12);

INSERT INTO round_rating(round_id, player_id, rating_change) VALUES
(1, 1, 3), (1, 2, 2), (1, 3, -1), (1, 4, -2), (2, 1, 3), (2, 2, -1), (3, 3, 0), (3, 4, 0);

INSERT INTO title_history(player_id, title_id, tournament_id) VALUES
(1, 1, 1), (1, 5, 2);

INSERT INTO player_class_history(player_id, tournament_id, player_class_id) VALUES
(1, 1, 5), (3, 1, 2);

INSERT INTO penalty(player_id, date_since, date_until, reason, tournament_id, arbiter_id, role_context_id) VALUES
(4, '2024-06-13', '2027-06-13', 'Rażące naruszenie regulaminu', 1, 11, (SELECT penalty_role_context_id FROM penalty_role_context WHERE name = 'Zawodnik'));

INSERT INTO rating_history(player_id, tournament_id, rating) VALUES
(1, 1, 2840), (2, 1, 2792), (3, 1, 2788), (4, 1, 2780);

INSERT INTO norm(player_id, tournament_id, title_id, date_until) VALUES
    (3, 1, 9, '2025-06-13'), (4, 2, 5, '2027-05-02');
