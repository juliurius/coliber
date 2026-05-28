CREATE OR REPLACE FUNCTION fn_close_tournament(tournament_id INTEGER)
RETURNS VOID AS $$
#variable_conflict use_variable
BEGIN
    IF EXISTS (
        SELECT 1
        FROM game g
        JOIN round r ON r.round_id = g.round_id
        LEFT JOIN game_over go  ON go.round_id = g.round_id AND go.white = g.white
        WHERE r.tournament_id = tournament_id AND go.round_id IS NULL
    ) THEN
        RAISE EXCEPTION 'Nie można zamknąć turnieju %: są partie bez wyniku', tournament_id;
    END IF;

    INSERT INTO rating_history(player_id, tournament_id, rating)
    SELECT
        tp.player_id,
        tp.tournament_id,
        p.rating + COALESCE(SUM(rr.rating_change), 0)
    FROM tournament_player tp
        JOIN player p ON p.player_id = tp.player_id
        JOIN round  r ON r.tournament_id = tp.tournament_id
        LEFT JOIN round_rating rr ON rr.round_id  = r.round_id AND rr.player_id = tp.player_id
    WHERE tp.tournament_id = tournament_id
    GROUP BY tp.player_id, tp.tournament_id, p.rating;
END;
$$ LANGUAGE plpgsql;