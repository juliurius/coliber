-- TEXT vs VARCHAR(n): https://www.depesz.com/index.php/2010/03/02/charx-vs-varcharx-vs-varchar-vs-text/ i https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_varchar(n)_by_default
-- SERIAL vs GENERATED AS IDENTITY: https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_serial
-- TIMESTAMPTZ: https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_timestamp_(without_time_zone)

CREATE TABLE city(
    city_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT NOT NULL,
    UNIQUE(name, location)
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
    base_time INTERVAL NOT NULL,
    move_time INTERVAL NOT NULL,
    extra_time INTERVAL NOT NULL DEFAULT INTERVAL '0s'
);

CREATE TABLE tournament_system(
   tournament_system_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   name TEXT NOT NULL UNIQUE
);

CREATE TABLE tournament(
    tournament_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tempo_id INT NOT NULL REFERENCES tempo,
    system_id INT NOT NULL REFERENCES tournament_system,
    time_start TIMESTAMPTZ NOT NULL,
    time_end TIMESTAMPTZ NOT NULL CHECK(time_start < time_end),
    city_id INT REFERENCES city,
    address TEXT,
    organiser INT NOT NULL REFERENCES player,
    main_arbiter INT NOT NULL REFERENCES player
);

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
    date_since DATE,
    class_id INT NOT NULL REFERENCES player_class,
    PRIMARY KEY (player_id, date_since)
);

CREATE TABLE arbiter_class_history(
    arbiter_id INT REFERENCES player,
    date_since DATE,
    class_id INT NOT NULL REFERENCES arbiter_class,
    PRIMARY KEY (arbiter_id, date_since)
);

CREATE TABLE round(
    round_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    time_start TIMESTAMPTZ NOT NULL,
    time_end TIMESTAMPTZ NOT NULL CHECK(time_start < time_end),
    tournament_id INT NOT NULL REFERENCES tournament
);

CREATE TABLE round_rating(
    round_id INT REFERENCES round,
    player_id INT REFERENCES player,
    rating_change INT,
    score NUMERIC(3, 1) NOT NULL,
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
    score NUMERIC(3, 1) NOT NULL DEFAULT 0,
    PRIMARY KEY(tournament_id, player_id)
);

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
    date_since DATE NOT NULL,
    PRIMARY KEY (player_id, title_id, date_since)
);

CREATE TABLE norm(
    player_id INT REFERENCES player,
    tournament_id INT REFERENCES tournament,
    title_id INT NOT NULL REFERENCES title,
    date_until DATE NOT NULL,
    PRIMARY KEY (player_id, tournament_id, title_id)
);

