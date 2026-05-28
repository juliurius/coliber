-- Podstawowe dane

\COPY title(name) FROM 'data/title.txt';
\COPY player_class(name) FROM 'data/player-class.txt';
\COPY arbiter_class(name) FROM 'data/arbiter-class.txt';
\COPY tournament_system(name) FROM 'data/tournament-system.txt';
\COPY tempo(name, description) FROM 'data/tempo.txt' WITH (FORMAT csv);
\COPY game_over_reason(description, win_score, lose_score) FROM 'data/game-over-reason.txt' WITH (FORMAT csv);
\COPY city(name, latitude, longitude) FROM 'data/city.txt' WITH (FORMAT csv);