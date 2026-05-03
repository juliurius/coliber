-- TEXT vs VARCHAR(n): https://www.depesz.com/index.php/2010/03/02/charx-vs-varcharx-vs-varchar-vs-text/ i https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_varchar(n)_by_default
-- SERIAL vs GENERATED AS IDENTITY: https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_serial
-- TIMESTAMPTZ: https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_timestamp_(without_time_zone)

CREATE TABLE city(
    city_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    latitude NUMERIC(9, 6) NOT NULL,
    longitude NUMERIC(10, 6) NOT NULL,
    UNIQUE(name, latitude, longitude)
);

CREATE TABLE player(
	player_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	name TEXT NOT NULL,
	surname TEXT NOT NULL,
    -- Trzeba utrzymywać zgodność triggerem. Trzymane dla indeksowania (rankingi)
	rating INT NOT NULL DEFAULT 1000
);

CREATE INDEX rating_idx ON player(rating, name);

CREATE TABLE club(
    club_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    city_id INT REFERENCES city,
    president INT REFERENCES player ON DELETE SET NULL
);

ALTER TABLE player ADD COLUMN club_id INT REFERENCES club ON DELETE SET NULL;

CREATE TABLE tempo(
    tempo_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT,
    description TEXT,
    UNIQUE(name, description)
);

CREATE TABLE tournament_system(
   tournament_system_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   name TEXT NOT NULL UNIQUE
);

-- main_arbiter musi się odnosić do sędziego
CREATE TABLE tournament(
    tournament_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    tempo_id INT NOT NULL REFERENCES tempo,
    system_id INT NOT NULL REFERENCES tournament_system,
    time_start TIMESTAMPTZ NOT NULL,
    time_end TIMESTAMPTZ NOT NULL CHECK(time_start < time_end),
    city_id INT REFERENCES city,
    address TEXT,
    organiser INT NOT NULL REFERENCES player,
    main_arbiter INT NOT NULL REFERENCES player
);

-- main_arbiter powinien być tu zarejestrowany
-- arbiter_id musi się odnosić do sędziego odpowiedniej klasy
CREATE TABLE tournament_arbiter(
    arbiter_id INT REFERENCES player,
    tournament_id INT REFERENCES tournament,
    PRIMARY KEY(tournament_id, arbiter_id)
);

CREATE TABLE rating_history(
    player_id INT REFERENCES player,
    tournament_id INT REFERENCES tournament,
    rating INT NOT NULL,
    PRIMARY KEY (player_id, tournament_id)
);

CREATE TABLE player_class(
    player_class_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE arbiter_class(
    arbiter_class_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE player_class_history(
    player_id INT REFERENCES player,
    tournament_id INT REFERENCES tournament,
    player_class_id INT NOT NULL REFERENCES player_class,
    PRIMARY KEY (player_id, tournament_id)
);

CREATE TABLE arbiter_class_history(
    arbiter_id INT REFERENCES player,
    date_since DATE,
    arbiter_class_id INT REFERENCES arbiter_class,
    PRIMARY KEY (arbiter_id, date_since)
);

CREATE TABLE round(
    round_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    -- Musi się odbywać w trakcie turnieju
    time_start TIMESTAMPTZ NOT NULL,
    time_end TIMESTAMPTZ NOT NULL CHECK(time_start < time_end),
    tournament_id INT NOT NULL REFERENCES tournament
);

CREATE TABLE round_rating(
    round_id INT REFERENCES round,
    player_id INT REFERENCES player,
    -- ryzyko niespójności danych
    rating_change INT,
    PRIMARY KEY(round_id, player_id)
);

CREATE TABLE game(
    round_id INT REFERENCES round,
    white INT REFERENCES player,
    black INT NOT NULL REFERENCES player,
    PRIMARY KEY(round_id, white),
    CHECK(white != black)
);

CREATE INDEX white_idx ON game(white);
CREATE INDEX black_idx ON game(black);

CREATE TABLE game_over_reason(
    game_over_reason_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description TEXT UNIQUE NOT NULL
);

-- arbiter_id musi się odnosić do sędziego odpowiedniej klasy
CREATE TABLE game_over(
    round_id INT REFERENCES round,
    white INT REFERENCES player,
    white_score NUMERIC(2, 1) NOT NULL,
    black_score NUMERIC(2, 1) NOT NULL,
    game_over_reason_id INT NOT NULL REFERENCES game_over_reason,
    arbiter_id INT NOT NULL REFERENCES player,
    PRIMARY KEY(round_id, white),
    FOREIGN KEY(round_id, white) REFERENCES game
);

CREATE TABLE tournament_player(
    tournament_id INT REFERENCES tournament,
    player_id INT REFERENCES player,
    -- Musi być uspójnione z ratingiem ostatniej rundy
    score NUMERIC(3, 1) NOT NULL DEFAULT 0,
    PRIMARY KEY(tournament_id, player_id)
);

-- arbiter_id musi się odnosić do sędziego odpowiedniej klasy
CREATE TABLE penalty(
    player_id INT REFERENCES player,
    date_since DATE NOT NULL,
    date_until DATE CHECK(date_since < date_until),
    reason TEXT,
    tournament_id INT REFERENCES tournament,
    arbiter_id INT REFERENCES player,
    PRIMARY KEY(player_id, date_since)
);

CREATE TABLE title(
    title_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE title_history(
    player_id INT REFERENCES player,
    title_id INT REFERENCES title,
    tournament_id INT NOT NULL REFERENCES tournament,
    PRIMARY KEY (player_id, title_id, tournament_id)
);

CREATE TABLE norm(
    player_id INT REFERENCES player,
    tournament_id INT REFERENCES tournament,
    title_id INT NOT NULL REFERENCES title,
    date_until DATE NOT NULL,
    PRIMARY KEY (player_id, tournament_id, title_id)
);

-- Podstawowe dane

\COPY title(name) FROM 'title.txt';
\COPY player_class(name) FROM 'player-class.txt';
\COPY arbiter_class(name) FROM 'arbiter-class.txt';
\COPY tournament_system(name) FROM 'tournament-system.txt';
\COPY tempo(name, description) FROM 'tempo.txt' WITH (FORMAT csv);
\COPY game_over_reason(description) FROM 'game-over-reason.txt';
\COPY city(name, latitude, longitude) FROM 'city.txt' WITH (FORMAT csv);

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
    ('Mistrzostwa Białystoku', 1, 3, '2024-06-12 11:00:00+02', '2024-06-14 11:00:00+02', 1, 'Sienkiewicza 55a lok. 70', 11, 11),
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

INSERT INTO round_rating(round_id, player_id, rating_change, score) VALUES
    (1, 1, 3, 1), (1, 2, 2, 1), (1, 3, -1, 0), (1, 4, -2, 0), (2, 1, 3, 2), (2, 2, -1, 1), (3, 3, 0, 0.5), (3, 4, 0, 0.5);

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
