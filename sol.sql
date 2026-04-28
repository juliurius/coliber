CREATE TABLE city(
    city_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT NOT NULL,
    UNIQUE(name, location)
);

CREATE TABLE player(
	player_id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	surname TEXT NOT NULL,
    -- Z tego sie trzeba wybronic
	rating INT NOT NULL DEFAULT 1000
);

CREATE TABLE club(
    club_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    city_id INT REFERENCES city,
    president INT REFERENCES player ON DELETE SET NULL
);

ALTER TABLE player ADD COLUMN club_id INT REFERENCES club ON DELETE SET NULL;

CREATE TABLE tempo(
    tempo_id SERIAL PRIMARY KEY,
    name TEXT,
    base_time INTERVAL NOT NULL,
    move_time INTERVAL NOT NULL,
    extra_time INTERVAL NOT NULL DEFAULT INTERVAL '0s'
);

CREATE TABLE tournament_system(
   tournament_system_id SERIAL PRIMARY KEY,
   name TEXT NOT NULL UNIQUE
);

CREATE TABLE tournament(
    tournament_id SERIAL PRIMARY KEY,
    tempo_id INT NOT NULL REFERENCES tempo,
    system_id INT NOT NULL REFERENCES tournament_system,
    date_from DATE NOT NULL,
    date_to DATE NOT NULL CHECK(date_from < date_to),
    city_id INT REFERENCES city,
    address TEXT,
    organiser INT NOT NULL REFERENCES player,
    main_arbiter INT NOT NULL REFERENCES player
);

CREATE TABLE tournament_arbiter(
    arbiter_id INTEGER REFERENCES player,
    tournament_id INTEGER REFERENCES tournament,
    PRIMARY KEY(tournament_id, arbiter_id)
);


CREATE TABLE rating_history(
    player_id INT REFERENCES player,
    tournament_id INT REFERENCES tournament,
    rating INT NOT NULL,
    PRIMARY KEY (player_id, tournament_id)
);

CREATE TABLE player_class(
    player_class_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE arbiter_class(
    arbiter_class_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE player_class_history(
    player_id INTEGER REFERENCES player,
    date_from DATE NOT NULL,
    class_id INTEGER NOT NULL REFERENCES player_class,
    PRIMARY KEY (player_id, date_from)
);

CREATE TABLE arbiter_class_history(
    arbiter_id INTEGER REFERENCES player,
    date_from DATE,
    class_id INTEGER NOT NULL REFERENCES arbiter_class,
    PRIMARY KEY (arbiter_id, date_from)
);

CREATE TABLE round(
    round_id SERIAL PRIMARY KEY,
    time_from TIMESTAMP NOT NULL,
    time_to TIMESTAMP NOT NULL CHECK(time_from < time_to),
    tournament_id INTEGER NOT NULL REFERENCES tournament
);

CREATE TABLE round_rating(
    round_id INTEGER REFERENCES round,
    player_id INTEGER REFERENCES player,
    rating_change INTEGER,
    score NUMERIC(2, 1) NOT NULL,
    PRIMARY KEY(round_id, player_id)
);

CREATE TABLE game(
    round_id INTEGER REFERENCES round,
    white INTEGER REFERENCES player,
    black INTEGER REFERENCES player,
    white_score NUMERIC(2, 1),
    black_score NUMERIC(2, 1),
    walkover BOOLEAN DEFAULT FALSE,
    reason TEXT,
    arbiter_id INTEGER NOT NULL REFERENCES player,
    PRIMARY KEY(round_id, white)
);

CREATE TABLE tournament_player(
    tournament_id INTEGER REFERENCES tournament,
    player_id INTEGER REFERENCES player,
    score INTEGER,
    PRIMARY KEY(tournament_id, player_id)
);

CREATE TABLE penalty(
    player_id INTEGER REFERENCES player,
    date_from DATE NOT NULL,
    date_to DATE CHECK(date_from < date_to),
    reason TEXT,
    tournament_id INTEGER REFERENCES tournament,
    arbiter_id INTEGER REFERENCES player,
    PRIMARY KEY(player_id, date_from)
);

CREATE TABLE title(
    title_id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
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
    title_id INTEGER NOT NULL REFERENCES title,
    date_to DATE,
    PRIMARY KEY (player_id, tournament_id, title_id)
);

