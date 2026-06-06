-- czy gracz jest sędzią
CREATE OR REPLACE FUNCTION trg_fn_require_arbiter()
RETURNS TRIGGER AS $$
DECLARE
    arbiter int := (to_jsonb(NEW) ->> TG_ARGV[0])::int;
BEGIN
      IF arbiter IS NOT NULL AND NOT EXISTS (SELECT 1 FROM arbiter_class_history WHERE arbiter_id = arbiter) THEN
          RAISE EXCEPTION 'Gracz % nie jest sędzią (brak wpisu w arbiter_class_history)', arbiter;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_tournament_main_arbiter ON tournament;
CREATE TRIGGER trg_tournament_main_arbiter BEFORE INSERT OR UPDATE ON tournament
FOR EACH ROW EXECUTE FUNCTION trg_fn_require_arbiter('main_arbiter');

DROP TRIGGER IF EXISTS trg_tournament_arbiter ON tournament_arbiter;
CREATE TRIGGER trg_tournament_arbiter BEFORE INSERT OR UPDATE ON tournament_arbiter
FOR EACH ROW EXECUTE FUNCTION trg_fn_require_arbiter('arbiter_id');

--TO DO : Ewentualnie trigger na odpowiednią klasę

-- Sędzia główny żyje wyłącznie w tournament.main_arbiter (nie jest dublowany w tournament_arbiter),
-- dlatego nie wymagamy już jego obecności w tournament_arbiter.
DROP TRIGGER IF EXISTS trg_main_arbiter_registered ON tournament;
DROP FUNCTION IF EXISTS trg_main_arbiter_registered();

DROP TRIGGER IF EXISTS trg_club_president_history_is_member ON club_president_history;
DROP FUNCTION IF EXISTS trg_fn_club_president_history_is_member();

-- aktualny klub zawodnika musi zgadzać się z historią membership
CREATE OR REPLACE FUNCTION trg_fn_player_club_matches_history()
RETURNS TRIGGER AS $$
DECLARE
    active_club INT;
    current_club INT;
BEGIN
    SELECT cmh.club_id INTO active_club
    FROM club_membership_history cmh
    WHERE cmh.player_id = NEW.player_id AND cmh.date_until IS NULL;

    SELECT p.club_id INTO current_club
    FROM player p
    WHERE p.player_id = NEW.player_id;

    IF current_club IS DISTINCT FROM active_club THEN
        RAISE EXCEPTION 'Aktualny klub zawodnika % nie zgadza się z historią członkostwa', NEW.player_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_player_club_matches_history_on_player ON player;
CREATE CONSTRAINT TRIGGER trg_player_club_matches_history_on_player AFTER INSERT OR UPDATE OF club_id ON player
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION trg_fn_player_club_matches_history();

-- aktualny prezes klubu musi zgadzać się z historią prezesów
CREATE OR REPLACE FUNCTION trg_fn_club_president_matches_history()
RETURNS TRIGGER AS $$
DECLARE
    active_president INT;
    current_president INT;
BEGIN
    SELECT cph.president INTO active_president
    FROM club_president_history cph
    WHERE cph.club_id = NEW.club_id AND cph.date_until IS NULL;

    SELECT c.president INTO current_president
    FROM club c
    WHERE c.club_id = NEW.club_id;

    IF current_president IS DISTINCT FROM active_president THEN
        RAISE EXCEPTION 'Aktualny prezes klubu % nie zgadza się z historią prezesów', NEW.club_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_club_president_matches_history_on_club ON club;
CREATE CONSTRAINT TRIGGER trg_club_president_matches_history_on_club AFTER INSERT OR UPDATE OF president ON club
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION trg_fn_club_president_matches_history();

-- czy runda odbywa się w terminie turnieju
CREATE OR REPLACE FUNCTION trg_fn_check_round_date()
RETURNS TRIGGER AS $$
DECLARE
    tr_time_start TIMESTAMPTZ;
    tr_time_end TIMESTAMPTZ;
BEGIN
    SELECT tr.time_start, tr.time_end INTO tr_time_start, tr_time_end
    FROM tournament tr WHERE tr.tournament_id = NEW.tournament_id;

    IF NEW.time_start < tr_time_start OR NEW.time_end > tr_time_end THEN
       RAISE EXCEPTION 'Runda % odbywa się w niedozwolonym terminie, turniej: %' , NEW.round_id, NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_fn_check_round_date ON round;
CREATE TRIGGER trg_fn_check_round_date BEFORE INSERT OR UPDATE ON round
FOR EACH ROW EXECUTE FUNCTION trg_fn_check_round_date();

-- czy nie przekraczamy zaplanowanej liczby rund turnieju
CREATE OR REPLACE FUNCTION trg_fn_check_round_count()
RETURNS TRIGGER AS $$
DECLARE
    max_rounds INT;
    current_rounds INT;
BEGIN
    SELECT tr.number_of_rounds INTO max_rounds
    FROM tournament tr WHERE tr.tournament_id = NEW.tournament_id;

    SELECT count(*) INTO current_rounds
    FROM round r WHERE r.tournament_id = NEW.tournament_id;

    IF current_rounds >= max_rounds THEN
       RAISE EXCEPTION 'Turniej % osiągnął zaplanowaną liczbę rund (%)', NEW.tournament_id, max_rounds;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_fn_check_round_count ON round;
CREATE TRIGGER trg_fn_check_round_count BEFORE INSERT ON round
FOR EACH ROW EXECUTE FUNCTION trg_fn_check_round_count();

-- nie można wygenerować nowej rundy, dopóki poprzednie rundy mają partie bez wyniku
CREATE OR REPLACE FUNCTION trg_fn_check_previous_rounds_finished()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM game g
        JOIN round r ON r.round_id = g.round_id
        LEFT JOIN game_over go ON go.round_id = g.round_id AND go.white = g.white
        WHERE r.tournament_id = NEW.tournament_id AND go.round_id IS NULL
    ) THEN
        RAISE EXCEPTION 'Nie można wygenerować nowej rundy w turnieju %: poprzednia runda ma partie bez wyniku', NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_fn_check_previous_rounds_finished ON round;
CREATE TRIGGER trg_fn_check_previous_rounds_finished BEFORE INSERT ON round
FOR EACH ROW EXECUTE FUNCTION trg_fn_check_previous_rounds_finished();

-- czy dany arbiter może wpisać wynik
CREATE OR REPLACE FUNCTION trg_fn_valid_arbiter_on_game()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
       SELECT 1
       FROM round r
       WHERE r.round_id = NEW.round_id
         AND fn_is_tournament_arbiter(NEW.arbiter_id, r.tournament_id)
    ) THEN
       RAISE EXCEPTION 'Nieuprawniony sędzia % do wpisywania wyniku', NEW.arbiter_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_fn_valid_arbiter_on_game ON game_over;
CREATE TRIGGER trg_fn_valid_arbiter_on_game BEFORE INSERT OR UPDATE ON game_over
FOR EACH ROW EXECUTE FUNCTION trg_fn_valid_arbiter_on_game();

-- kara musi dotyczyć osoby i sędziego z danego turnieju
CREATE OR REPLACE FUNCTION trg_fn_valid_penalty_context()
RETURNS TRIGGER AS $$
DECLARE
    role_name TEXT;
BEGIN
    SELECT prc.name INTO role_name
    FROM penalty_role_context prc
    WHERE prc.penalty_role_context_id = NEW.role_context_id;

    IF NOT fn_is_tournament_arbiter(NEW.arbiter_id, NEW.tournament_id) THEN
        RAISE EXCEPTION 'Sędzia % nie jest przypisany do turnieju %', NEW.arbiter_id, NEW.tournament_id;
    END IF;

    IF role_name = 'Zawodnik' AND NOT EXISTS (
        SELECT 1
        FROM tournament_player tp
        WHERE tp.tournament_id = NEW.tournament_id AND tp.player_id = NEW.player_id
    ) THEN
        RAISE EXCEPTION 'Zawodnik % nie jest zapisany do turnieju %', NEW.player_id, NEW.tournament_id;
    END IF;

    IF role_name = 'Sędzia' AND NOT fn_is_tournament_arbiter(NEW.player_id, NEW.tournament_id) THEN
        RAISE EXCEPTION 'Sędzia % nie jest przypisany do turnieju %', NEW.player_id, NEW.tournament_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_penalty_arbiter ON penalty;
DROP TRIGGER IF EXISTS trg_valid_penalty_context ON penalty;
CREATE TRIGGER trg_valid_penalty_context BEFORE INSERT OR UPDATE ON penalty
FOR EACH ROW EXECUTE FUNCTION trg_fn_valid_penalty_context();

-- Gracze w partii muszą być zapisani na turniej (tournament_player)
CREATE OR REPLACE FUNCTION trg_fn_game_players_registered()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE v_tournament int;
BEGIN
    SELECT r.tournament_id INTO v_tournament FROM round r WHERE r.round_id = NEW.round_id;
    IF NOT EXISTS (SELECT 1 FROM tournament_player WHERE tournament_id = v_tournament AND player_id = NEW.white) THEN
        RAISE EXCEPTION 'Biały % nie jest zapisany na turniej %', NEW.white, v_tournament;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM tournament_player WHERE tournament_id = v_tournament AND player_id = NEW.black) THEN
        RAISE EXCEPTION 'Czarny % nie jest zapisany na turniej %', NEW.black, v_tournament;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_game_players_registered ON game;
CREATE TRIGGER trg_game_players_registered BEFORE INSERT OR UPDATE ON game
FOR EACH ROW EXECUTE FUNCTION trg_fn_game_players_registered();

-- Każdy gracz gra maksymalnie jedną partię w rundzie (białymi lub czarnymi)
CREATE OR REPLACE FUNCTION trg_fn_one_game_per_round()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM game g
        WHERE g.round_id = NEW.round_id AND g.white <> NEW.white AND (NEW.white IN (g.white, g.black) OR NEW.black IN (g.white, g.black))
    ) THEN
        RAISE EXCEPTION 'Gracz z pary (%/%) gra już w rundzie %', NEW.white, NEW.black, NEW.round_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_one_game_per_round ON game;
CREATE TRIGGER trg_one_game_per_round BEFORE INSERT OR UPDATE ON game
FOR EACH ROW EXECUTE FUNCTION trg_fn_one_game_per_round();

-- Po zakończeniu turnieju (są wpisy w rating_history) nie można dodawać rund
CREATE OR REPLACE FUNCTION trg_fn_no_round_after_close()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM rating_history WHERE tournament_id = NEW.tournament_id) THEN
        RAISE EXCEPTION 'Turniej % jest zakończony — nie można dodać rundy', NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_no_round_after_close ON round;
CREATE TRIGGER trg_no_round_after_close BEFORE INSERT ON round
FOR EACH ROW EXECUTE FUNCTION trg_fn_no_round_after_close();

-- Po zakończeniu turnieju nie można dodawać ani zmieniać wyników partii
CREATE OR REPLACE FUNCTION trg_fn_no_result_after_close()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE v_tournament int;
BEGIN
    SELECT r.tournament_id INTO v_tournament FROM round r WHERE r.round_id = NEW.round_id;
    IF EXISTS (SELECT 1 FROM rating_history WHERE tournament_id = v_tournament) THEN
        RAISE EXCEPTION 'Turniej % jest zakończony — nie można zmieniać wyników', v_tournament;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_no_result_after_close ON game_over;
CREATE TRIGGER trg_no_result_after_close BEFORE INSERT OR UPDATE ON game_over
FOR EACH ROW EXECUTE FUNCTION trg_fn_no_result_after_close();

-- Po rozpoczęciu turnieju nie można już dopisywać zawodników
CREATE OR REPLACE FUNCTION trg_fn_no_player_after_start()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF (SELECT started FROM tournament WHERE tournament_id = NEW.tournament_id) THEN
        RAISE EXCEPTION 'Turniej % jest już rozpoczęty — nie można dodać zawodnika', NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_no_player_after_start ON tournament_player;
CREATE TRIGGER trg_no_player_after_start BEFORE INSERT ON tournament_player
FOR EACH ROW EXECUTE FUNCTION trg_fn_no_player_after_start();

-- Rundy można generować dopiero po rozpoczęciu turnieju
CREATE OR REPLACE FUNCTION trg_fn_round_requires_start()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF NOT (SELECT started FROM tournament WHERE tournament_id = NEW.tournament_id) THEN
        RAISE EXCEPTION 'Turniej % nie został rozpoczęty — nie można wygenerować rundy', NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_round_requires_start ON round;
CREATE TRIGGER trg_round_requires_start BEFORE INSERT ON round
FOR EACH ROW EXECUTE FUNCTION trg_fn_round_requires_start();

-- Ukarany zawodnik nie może zostać dopisany do turnieju (kara w okresie turnieju)
CREATE OR REPLACE FUNCTION trg_fn_no_penalised_player()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE ts DATE; te DATE;
BEGIN
    SELECT t.time_start::date, t.time_end::date INTO ts, te
    FROM tournament t WHERE t.tournament_id = NEW.tournament_id;
    IF fn_has_penalty_in_period(NEW.player_id, ts, te) THEN
        RAISE EXCEPTION 'Zawodnik % ma karę w okresie turnieju % — nie może zostać dopisany', NEW.player_id, NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_no_penalised_player ON tournament_player;
CREATE TRIGGER trg_no_penalised_player BEFORE INSERT ON tournament_player
FOR EACH ROW EXECUTE FUNCTION trg_fn_no_penalised_player();

-- Ukarany sędzia nie może zostać przypisany do turnieju (kara w okresie turnieju)
CREATE OR REPLACE FUNCTION trg_fn_no_penalised_arbiter()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
DECLARE ts DATE; te DATE;
BEGIN
    SELECT t.time_start::date, t.time_end::date INTO ts, te
    FROM tournament t WHERE t.tournament_id = NEW.tournament_id;
    IF fn_has_penalty_in_period(NEW.arbiter_id, ts, te) THEN
        RAISE EXCEPTION 'Sędzia % ma karę w okresie turnieju % — nie może zostać przypisany', NEW.arbiter_id, NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_no_penalised_arbiter ON tournament_arbiter;
CREATE TRIGGER trg_no_penalised_arbiter BEFORE INSERT ON tournament_arbiter
FOR EACH ROW EXECUTE FUNCTION trg_fn_no_penalised_arbiter();

-- Ukarany sędzia główny nie może zostać przypisany (kara w okresie turnieju)
CREATE OR REPLACE FUNCTION trg_fn_no_penalised_main_arbiter()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
    IF fn_has_penalty_in_period(NEW.main_arbiter, NEW.time_start::date, NEW.time_end::date) THEN
        RAISE EXCEPTION 'Sędzia główny % ma karę w okresie turnieju — nie może zostać przypisany', NEW.main_arbiter;
    END IF;
    RETURN NEW;
END;
$$;

DROP TRIGGER IF EXISTS trg_no_penalised_main_arbiter ON tournament;
CREATE TRIGGER trg_no_penalised_main_arbiter BEFORE INSERT OR UPDATE ON tournament
FOR EACH ROW EXECUTE FUNCTION trg_fn_no_penalised_main_arbiter();
