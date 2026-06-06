-- Nazwa tempa jest jednocześnie kategorią rankingu.
CREATE OR REPLACE FUNCTION fn_tournament_rating_category(p_tournament_id INTEGER)
RETURNS TEXT AS $$
    SELECT CASE
        WHEN te.name = 'Klasyczne' THEN 'classical'
        WHEN te.name = 'Szybkie' THEN 'rapid'
        WHEN te.name = 'Błyskawiczne' THEN 'blitz'
    END
    FROM tournament t
    JOIN tempo te ON te.tempo_id = t.tempo_id
    WHERE t.tournament_id = p_tournament_id;
$$ LANGUAGE SQL STABLE;

-- Ranking gracza w kategorii zgodnej z tempem danego turnieju.
CREATE OR REPLACE FUNCTION fn_player_tournament_rating(p_player_id INTEGER, p_tournament_id INTEGER)
RETURNS INTEGER AS $$
    SELECT CASE fn_tournament_rating_category(p_tournament_id)
        WHEN 'classical' THEN p.rating_classical
        WHEN 'rapid'     THEN p.rating_rapid
        WHEN 'blitz'     THEN p.rating_blitz
    END
    FROM player p
    WHERE p.player_id = p_player_id;
$$ LANGUAGE SQL STABLE;

-- Czy gracz ma karę nakładającą się na podany okres (np. czas trwania turnieju).
CREATE OR REPLACE FUNCTION fn_has_penalty_in_period(p_player_id INTEGER, p_from DATE, p_to DATE)
RETURNS BOOLEAN AS $$
    SELECT EXISTS (
        SELECT 1 FROM penalty pen
        WHERE pen.player_id = p_player_id
          AND pen.date_since <= p_to
          AND (pen.date_until IS NULL OR pen.date_until >= p_from)
    );
$$ LANGUAGE SQL STABLE;

-- Czy gracz pełni rolę sędziego w turnieju: sędzia główny (tournament.main_arbiter)
-- ALBO sędzia pomocniczy (wpis w tournament_arbiter). Sędzia główny żyje wyłącznie w tournament.
CREATE OR REPLACE FUNCTION fn_is_tournament_arbiter(p_player_id INTEGER, p_tournament_id INTEGER)
RETURNS BOOLEAN AS $$
    SELECT EXISTS (
        SELECT 1 FROM tournament t
        WHERE t.tournament_id = p_tournament_id AND t.main_arbiter = p_player_id
    ) OR EXISTS (
        SELECT 1 FROM tournament_arbiter ta
        WHERE ta.tournament_id = p_tournament_id AND ta.arbiter_id = p_player_id
    );
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION fn_close_tournament(tournament_id INTEGER)
RETURNS VOID AS $$
#variable_conflict use_variable
DECLARE
    category TEXT := fn_tournament_rating_category(tournament_id);
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

    -- ranking po turnieju = aktualny ranking gracza w kategorii tempa + suma zmian z rund
    INSERT INTO rating_history(player_id, tournament_id, rating)
    SELECT
        tp.player_id,
        tp.tournament_id,
        CASE category
            WHEN 'classical' THEN p.rating_classical
            WHEN 'rapid'     THEN p.rating_rapid
            WHEN 'blitz'     THEN p.rating_blitz
        END + COALESCE(SUM(rr.rating_change), 0)
    FROM tournament_player tp
        JOIN player p ON p.player_id = tp.player_id
        JOIN round  r ON r.tournament_id = tp.tournament_id
        LEFT JOIN round_rating rr ON rr.round_id  = r.round_id AND rr.player_id = tp.player_id
    WHERE tp.tournament_id = tournament_id
    GROUP BY tp.player_id, tp.tournament_id, p.rating_classical, p.rating_rapid, p.rating_blitz;
END;
$$ LANGUAGE plpgsql;
