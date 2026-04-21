CREATE TABLE player(
	player_id SERIAL PRIMARY KEY,
	name VARCHAR(30) NOT NULL,
	surname VARCHAR(50) NOT NULL,
	rating INT DEFAULT 1000,
	country INT REFERENCES country,
    club_id INT REFERENCES club
);

CREATE TABLE country(
	country_id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL
);

CREATE TABLE club(
    club_id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    city_id INT REFERENCES city,
    president INT
);

CREATE TABLE city(
    city_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location TEXT
);

CREATE TABLE tempo(
    tempo_id SERIAL PRIMARY KEY,
    name TEXT,
    base_time INTERVAL,
    move_time INTERVAL,
    extra_time INTERVAL
);

CREATE TABLE tournament(
    tournament_id SERIAL PRIMARY KEY,
    tempo_id INT REFERENCES tempo,
    system_id INT REFERENCES tournament_system,
    date_from DATE,
    date_to DATE,
    city_id INT REFERENCES city,
    address TEXT,
    organiser INT REFERENCES player,
    main_arbiter INT REFERENCES player
);

CREATE TABLE tournament_system(
   tournament_system_id SERIAL PRIMARY KEY,
   name text NOT NULL
);

CREATE TABLE arbiter_tournament(
    arbiter_id INTEGER REFERENCES player,
    tournament_id INTEGER REFERENCES tournament
);


CREATE TABLE rating_history(
    player_id INT REFERENCES player,
    rating SERIAL NOT NULL,
    date_from DATE NOT NULL,
    tournament_id INT REFERENCES tournament,
    PRIMARY KEY (player_id, rating)
);

CREATE TABLE player_class(
    player_class_id SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE arbiter_class(
    arbiter_class_id SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE player_class_history(
    player_id INTEGER REFERENCES player,
    date_from DATE,
    class_id INTEGER REFERENCES player_class,
    PRIMARY KEY (player_id, date_from)
);

CREATE TABLE arbiter_class_history(
    arbiter_id INTEGER REFERENCES player,
    date_from DATE,
    class_id INTEGER REFERENCES arbiter_class,
    PRIMARY KEY (arbiter_id, date_from)
);

CREATE TABLE round(
    round_id SERIAL PRIMARY KEY,
    date_from TIMESTAMP,
    date_to TIMESTAMP,
    tournament_id INTEGER REFERENCES tournament
);

CREATE TABLE round_rating(
    round_id INTEGER REFERENCES round,
    player_id INTEGER REFERENCES player,
    rating_change INTEGER NOT NULL,
    score NUMERIC(2, 1) NOT NULL
);

CREATE TABLE game(
    round_id INTEGER REFERENCES round,
    white INTEGER REFERENCES player,
    black INTERVAL REFERENCES player,
    white_score NUMERIC(2, 1),
    black_score NUMERIC(2, 1),
    walkover BOOLEAN DEFAULT FALSE,
    reason TEXT,
    arbiter_id INTEGER REFERENCES player
);

CREATE TABLE tournament_player(
    tournament_id INTEGER REFERENCES tournament,
    player_id INTEGER REFERENCES player,
    score INTEGER
);

CREATE TABLE penalty(
    player_id INTEGER REFERENCES player,
    date_from DATE,
    date_to DATE,
    reason TEXT,
    tournament_id INTEGER REFERENCES tournament,
    arbiter_id INTEGER REFERENCES player
);

CREATE TABLE title(
    title_id SERIAL PRIMARY KEY,
    name TEXT
);

CREATE TABLE title_history(
    player_id INTEGER REFERENCES player,
    title_id INTEGER REFERENCES title,
    date_from DATE NOT NULL,
    PRIMARY KEY (player_id, title_id, date_from)
);

CREATE TABLE norm(
    player_id INTEGER REFERENCES player,
    tournament_id INTEGER REFERENCES tournament,
    date_to DATE,
    title_id INTEGER REFERENCES title,
    PRIMARY KEY (player_id, tournament_id, title_id)
);

