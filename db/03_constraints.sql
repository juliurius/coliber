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

-- czt sędzia główny jest zapisany jako sędzia w turnieju
CREATE OR REPLACE FUNCTION trg_main_arbiter_registered()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
       SELECT 1
       FROM tournament_arbiter ta
       WHERE ta.tournament_id = NEW.tournament_id AND ta.arbiter_id = NEW.main_arbiter
    )  THEN
       RAISE EXCEPTION 'Sędzia główny % nie jest zapisany w tournament_arbiter turnieju %',NEW.main_arbiter, NEW.tournament_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_main_arbiter_registered ON tournament;
CREATE CONSTRAINT TRIGGER trg_main_arbiter_registered AFTER INSERT OR UPDATE ON tournament
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION trg_main_arbiter_registered();

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
       INNER JOIN tournament_arbiter ta ON r.tournament_id = ta.tournament_id
       WHERE r.round_id = NEW.round_id AND ta.arbiter_id = NEW.arbiter_id
    ) THEN
       RAISE EXCEPTION 'Nieuprawniony sędzia % do wpisywania wyniku', NEW.arbiter_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE PLPGSQL;

DROP TRIGGER IF EXISTS trg_fn_valid_arbiter_on_game ON game_over;
CREATE TRIGGER trg_fn_valid_arbiter_on_game BEFORE INSERT OR UPDATE ON game_over
FOR EACH ROW EXECUTE FUNCTION trg_fn_valid_arbiter_on_game();

-- penalty.arbiter_id musi być sędzią
DROP TRIGGER IF EXISTS trg_penalty_arbiter ON penalty;
CREATE TRIGGER trg_penalty_arbiter BEFORE INSERT OR UPDATE ON penalty
FOR EACH ROW EXECUTE FUNCTION trg_fn_require_arbiter('arbiter_id');

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