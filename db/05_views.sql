-- Globalny ranking graczy
CREATE OR REPLACE VIEW v_ranking AS
SELECT
    RANK() OVER (ORDER BY p.rating DESC) AS position,
    p.player_id, p.name, p.surname, p.rating
FROM player p;

-- Tabela wyników w obrębie turnieju
CREATE OR REPLACE VIEW v_tournament_standings AS
SELECT
    tp.tournament_id,
    RANK() OVER (PARTITION BY tp.tournament_id ORDER BY tp.score DESC) AS place,
    tp.player_id, p.name, p.surname, tp.score
FROM tournament_player tp
JOIN player p ON p.player_id = tp.player_id;

-- Statystyki gracza
CREATE OR REPLACE VIEW v_player_stats AS
SELECT
    p.player_id, p.name, p.surname, p.rating,
    c.name AS club,
    (SELECT count(*) FROM tournament_player tp WHERE tp.player_id = p.player_id) AS tournaments_played,
    (SELECT count(*) FROM title_history th WHERE th.player_id = p.player_id) AS titles_count,
    (SELECT count(*) FROM penalty pen
       WHERE pen.player_id = p.player_id AND (pen.date_until IS NULL OR pen.date_until > CURRENT_DATE)) AS active_penalties
FROM player p
LEFT JOIN club c ON c.club_id = p.club_id;
