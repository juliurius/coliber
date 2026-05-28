
-- update rankingu po zakończniu turnieju
CREATE OR REPLACE FUNCTION trg_rating_history_update_player_rating()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    UPDATE player SET rating = NEW.rating WHERE player_id = NEW.player_id;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_rating_history_update_player_rating ON rating_history;
CREATE TRIGGER trg_rating_history_update_player_rating
  AFTER INSERT ON rating_history
  FOR EACH ROW EXECUTE FUNCTION trg_rating_history_update_player_rating();

-- sprawdzanie score
CREATE OR REPLACE FUNCTION trg_sync_tournament_score()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
#variable_conflict use_variable
DECLARE
    round INTEGER = COALESCE(NEW.round_id, OLD.round_id);
    white INTEGER = COALESCE(NEW.white,    OLD.white);
    tournament INTEGER;
    black INTEGER;
BEGIN
    SELECT r.tournament_id INTO tournament FROM round r WHERE r.round_id = round;
    SELECT g.black INTO black FROM game g WHERE g.round_id = round AND g.white = white;

    UPDATE tournament_player tp
    SET score =
        COALESCE((SELECT SUM(go.white_score)
                  FROM game_over go JOIN round r ON r.round_id = go.round_id
                  WHERE r.tournament_id = tp.tournament_id AND go.white = tp.player_id), 0)
        + COALESCE((SELECT SUM(go.black_score)
                  FROM game_over go
                  JOIN game  g ON g.round_id = go.round_id AND g.white = go.white
                  JOIN round r ON r.round_id = go.round_id
                  WHERE r.tournament_id = tp.tournament_id AND g.black = tp.player_id), 0)
    WHERE tp.tournament_id = tournament AND tp.player_id IN (white, black);

    RETURN NULL;
END;
$$;

DROP TRIGGER IF EXISTS trg_sync_tournament_score ON game_over;
CREATE TRIGGER trg_sync_tournament_score
AFTER INSERT OR UPDATE OR DELETE ON game_over
FOR EACH ROW EXECUTE FUNCTION trg_sync_tournament_score();