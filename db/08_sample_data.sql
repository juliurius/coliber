-- 08_sample_data.sql — rozbudowane dane przykładowe.
-- Spójne ze wszystkimi triggerami z 03_constraints.sql i 04_consistency.sql.
-- Zakłada świeżą instalację po 07 (gracze 1..102, miasta 1..18, kluby 1..2, turnieje 1..3).

-- =========================================================================
-- MIASTA (id 19..40)
-- =========================================================================
INSERT INTO city(name, latitude, longitude) VALUES
('Gdynia',          54.518900, 18.530500),
('Sopot',           54.441800, 18.560100),
('Częstochowa',     50.811800, 19.120300),
('Radom',           51.402700, 21.147100),
('Sosnowiec',       50.286300, 19.104000),
('Płock',           52.546800, 19.706400),
('Elbląg',          54.152200, 19.408800),
('Tarnów',          50.012100, 20.985800),
('Koszalin',        54.194300, 16.172200),
('Kalisz',          51.761100, 18.091000),
('Legnica',         51.207000, 16.161900),
('Słupsk',          54.464100, 17.028700),
('Jaworzno',        50.205000, 19.273100),
('Nowy Sącz',       49.617400, 20.715200),
('Konin',           52.223000, 18.251100),
('Piła',            53.151500, 16.738300),
('Inowrocław',      52.797900, 18.261100),
('Lubin',           51.400900, 16.201400),
('Ostrołęka',       53.085900, 21.578000),
('Suwałki',         54.111500, 22.930900),
('Chełm',           51.143100, 23.471600),
('Mielec',          50.287000, 21.423700);

-- =========================================================================
-- GRACZE (id 103..162): 103..114 to przyszli sędziowie, 115..162 zawodnicy
-- =========================================================================
INSERT INTO player(name, surname, rating_classical) VALUES
-- sędziowie (103..114)
('Marek',     'Sędziński',   1980),
('Agnieszka', 'Arbitrowicz', 1960),
('Tomasz',    'Regulski',    2010),
('Barbara',   'Werdykt',     1890),
('Krzysztof', 'Protokół',    2040),
('Joanna',    'Zegarek',     1850),
('Andrzej',   'Notacja',     1995),
('Ewa',       'Remisowska',  1820),
('Paweł',     'Rozjemca',    1930),
('Katarzyna', 'Tempo',       1875),
('Robert',    'Pat',         2025),
('Magdalena', 'Mat',         1905),
-- zawodnicy (115..162)
('Jan',       'Kowalczyk',   2310),
('Piotr',     'Lewandowski', 2280),
('Adam',      'Wójcik',      2255),
('Michał',    'Kamiński',    2230),
('Łukasz',    'Zieliński',   2205),
('Marcin',    'Szymański',   2180),
('Jakub',     'Woźniak',     2155),
('Grzegorz',  'Dąbrowski',   2130),
('Kamil',     'Kozłowski',   2105),
('Mateusz',   'Jankowski',   2080),
('Bartosz',   'Mazur',       2055),
('Damian',    'Krawczyk',    2030),
('Patryk',    'Piotrowski',  2005),
('Sebastian', 'Grabowski',   1980),
('Dawid',     'Nowicki',     1955),
('Hubert',    'Pawłowski',   1930),
('Oskar',     'Michalski',   1905),
('Filip',     'Adamczyk',    1880),
('Igor',      'Dudek',       1855),
('Wiktor',    'Zając',       1830),
('Szymon',    'Wieczorek',   1805),
('Antoni',    'Jabłoński',   1780),
('Aleksander','Król',        1755),
('Franciszek','Majewski',    1730),
('Aleksandra','Olszewska',   1705),
('Maria',     'Jaworska',    1680),
('Zuzanna',   'Wróbel',      1655),
('Julia',     'Malinowska',  1630),
('Maja',      'Pawlak',      1605),
('Hanna',     'Witkowska',   1580),
('Lena',      'Walczak',     1555),
('Zofia',     'Stępień',     1530),
('Nadia',     'Górska',      1505),
('Oliwia',    'Rutkowska',   1480),
('Amelia',    'Michalak',    1455),
('Wiktoria',  'Sikora',      1430),
('Natalia',   'Baran',       1405),
('Karolina',  'Duda',        1380),
('Weronika',  'Kaczmarek',   1355),
('Gabriela',  'Wilk',        1330),
('Emilia',    'Sokołowska',  1305),
('Tymoteusz', 'Sawicki',     2295),
('Borys',     'Czarnecki',   2270),
('Leon',      'Sobczak',     2245),
('Gustaw',    'Urbański',    2220),
('Henryk',    'Lis',         2195),
('Konrad',    'Baranowski',  2170),
('Wojciech',  'Tomczak',     2150);

-- na starcie wszystkie rankingi równe klasycznemu, potem lekkie zróżnicowanie
UPDATE player SET rating_rapid = rating_classical, rating_blitz = rating_classical WHERE player_id >= 103;
UPDATE player SET rating_rapid = rating_classical - 40, rating_blitz = rating_classical - 75 WHERE player_id >= 103;

-- =========================================================================
-- SĘDZIOWIE: nadanie klas (gracze 103..114)
-- =========================================================================
INSERT INTO arbiter_class_history(arbiter_id, arbiter_class_id, date_since) VALUES
(103, 5, '2014-03-01'),
(104, 6, '2016-06-15'),
(105, 7, '2012-09-20'),
(106, 5, '2018-01-10'),
(107, 4, '2019-04-22'),
(108, 6, '2015-11-05'),
(109, 3, '2020-02-18'),
(110, 5, '2017-07-30'),
(111, 6, '2013-05-12'),
(112, 2, '2021-03-03'),
(113, 4, '2016-10-25'),
(114, 1, '2022-08-08');

-- =========================================================================
-- KLUBY (id 3..8) z historią członkostwa i prezesów
-- prezes jest też członkiem; aktywne wpisy muszą zgadzać się z player.club_id i club.president
-- =========================================================================
INSERT INTO club(name, city_id) VALUES
('Polonia Warszawa', 16),   -- club 3
('Hetman Wrocław',   17),   -- club 4
('Skoczek Poznań',   12),   -- club 5
('Wieża Kraków',      7),   -- club 6
('Goniec Gdańsk',     3),   -- club 7
('Roszada Lublin',    8);   -- club 8

-- prezesi (po jednym na klub)
INSERT INTO club_president_history(club_id, president, date_since) VALUES
(3, 115, '2022-01-01'),
(4, 119, '2022-01-01'),
(5, 123, '2022-01-01'),
(6, 127, '2022-01-01'),
(7, 131, '2022-01-01'),
(8, 135, '2022-01-01');
UPDATE club SET president = 115 WHERE club_id = 3;
UPDATE club SET president = 119 WHERE club_id = 4;
UPDATE club SET president = 123 WHERE club_id = 5;
UPDATE club SET president = 127 WHERE club_id = 6;
UPDATE club SET president = 131 WHERE club_id = 7;
UPDATE club SET president = 135 WHERE club_id = 8;

-- członkowie (po 4 na klub: 115..138)
INSERT INTO club_membership_history(player_id, club_id, date_since) VALUES
(115,3,'2022-01-01'),(116,3,'2022-02-01'),(117,3,'2022-03-01'),(118,3,'2022-04-01'),
(119,4,'2022-01-01'),(120,4,'2022-02-01'),(121,4,'2022-03-01'),(122,4,'2022-04-01'),
(123,5,'2022-01-01'),(124,5,'2022-02-01'),(125,5,'2022-03-01'),(126,5,'2022-04-01'),
(127,6,'2022-01-01'),(128,6,'2022-02-01'),(129,6,'2022-03-01'),(130,6,'2022-04-01'),
(131,7,'2022-01-01'),(132,7,'2022-02-01'),(133,7,'2022-03-01'),(134,7,'2022-04-01'),
(135,8,'2022-01-01'),(136,8,'2022-02-01'),(137,8,'2022-03-01'),(138,8,'2022-04-01');
UPDATE player SET club_id = 3 WHERE player_id IN (115,116,117,118);
UPDATE player SET club_id = 4 WHERE player_id IN (119,120,121,122);
UPDATE player SET club_id = 5 WHERE player_id IN (123,124,125,126);
UPDATE player SET club_id = 6 WHERE player_id IN (127,128,129,130);
UPDATE player SET club_id = 7 WHERE player_id IN (131,132,133,134);
UPDATE player SET club_id = 8 WHERE player_id IN (135,136,137,138);

-- HISTORIA: wcześniejsze (zamknięte) członkostwa — zawodnicy zmieniali kluby.
-- Aktywne członkostwo (date_until IS NULL) zostaje powyższe; tu tylko zakończone kadencje.
INSERT INTO club_membership_history(player_id, club_id, date_since, date_until) VALUES
(115, 4, '2019-01-01', '2021-12-31'),
(116, 5, '2018-06-01', '2021-12-31'),
(117, 6, '2020-01-01', '2021-12-31'),
(118, 7, '2017-01-01', '2021-12-31'),
(119, 3, '2019-03-01', '2021-12-31'),
(120, 6, '2020-02-01', '2021-12-31'),
(123, 7, '2018-01-01', '2021-12-31'),
(124, 8, '2019-05-01', '2021-12-31'),
(127, 8, '2017-09-01', '2021-12-31'),
(128, 5, '2018-03-01', '2021-12-31'),
(131, 3, '2019-01-01', '2021-12-31'),
(132, 7, '2020-04-01', '2021-12-31'),
(135, 4, '2018-01-01', '2021-12-31'),
(136, 3, '2019-06-01', '2021-12-31'),
-- jeden zawodnik z dłuższą, dwuetapową historią
(115, 6, '2016-01-01', '2018-12-31'),
(119, 7, '2016-06-01', '2019-02-28');

-- HISTORIA: poprzedni prezesi (zakończone kadencje)
INSERT INTO club_president_history(club_id, president, date_since, date_until) VALUES
(3, 116, '2019-01-01', '2021-12-31'),
(4, 120, '2019-01-01', '2021-12-31'),
(5, 124, '2018-01-01', '2021-12-31'),
(6, 128, '2019-01-01', '2021-12-31'),
(7, 132, '2018-06-01', '2021-12-31'),
(8, 136, '2017-01-01', '2021-12-31');

-- =========================================================================
-- TYTUŁY ZDOBYTE WCZEŚNIEJ (poza naszymi turniejami) — najsilniejsi już utytułowani.
-- Dzięki temu nie zbierają norm na najniższe tytuły, a mogą zdobywać normy na wyższe.
-- (turniej 1 wskazany jako miejsce zdobycia — title_history nie wymaga udziału w turnieju)
-- =========================================================================
INSERT INTO title_history(player_id, title_id, tournament_id) VALUES
(115, 11, 1),   -- GM
(159, 10, 1),   -- IM
(116,  9, 1),   -- FM
(160,  8, 1);   -- CM

-- =========================================================================
-- TURNIEJE — pomocnicza funkcja (usuwana na końcu)
-- Tworzy turniej, zapisuje sędziów pomocniczych i zawodników, opcjonalnie startuje,
-- rozgrywa p_play_rounds rund metodą kółkową (round-robin) i opcjonalnie zamyka.
-- =========================================================================
CREATE OR REPLACE FUNCTION _seed_tournament(
    p_name TEXT, p_tempo INT, p_system INT, p_rounds INT,
    p_start TIMESTAMPTZ, p_end TIMESTAMPTZ, p_city INT, p_address TEXT,
    p_organiser INT, p_main_arbiter INT,
    p_players INT[], p_extra_arbiters INT[],
    p_play_rounds INT, p_started BOOLEAN, p_close BOOLEAN
) RETURNS INT
LANGUAGE plpgsql AS $$
DECLARE
    v_tid INT; v_rid INT;
    a INT; r INT; i INT;
    n INT; m INT;
    fixed INT; rot INT[];
    w INT; b INT; rw INT; rb INT; ww BOOLEAN;
    rstart TIMESTAMPTZ;
BEGIN
    INSERT INTO tournament(name, tempo_id, system_id, number_of_rounds,
                           time_start, time_end, city_id, address, organiser, main_arbiter)
    VALUES (p_name, p_tempo, p_system, p_rounds, p_start, p_end, p_city, p_address, p_organiser, p_main_arbiter)
    RETURNING tournament_id INTO v_tid;

    IF p_extra_arbiters IS NOT NULL THEN
        FOREACH a IN ARRAY p_extra_arbiters LOOP
            INSERT INTO tournament_arbiter(tournament_id, arbiter_id) VALUES (v_tid, a);
        END LOOP;
    END IF;

    IF p_players IS NOT NULL THEN
        FOREACH a IN ARRAY p_players LOOP
            INSERT INTO tournament_player(tournament_id, player_id) VALUES (v_tid, a);
        END LOOP;
    END IF;

    IF p_started THEN
        UPDATE tournament SET started = TRUE WHERE tournament_id = v_tid;
    END IF;

    IF p_play_rounds > 0 THEN
        n := array_length(p_players, 1);
        fixed := p_players[1];
        rot := p_players[2:n];   -- pozostali (długość n-1)
        m := n - 1;
        FOR r IN 1..p_play_rounds LOOP
            rstart := p_start + (r * interval '12 hours');
            INSERT INTO round(time_start, time_end, tournament_id)
            VALUES (rstart, rstart + interval '3 hours', v_tid)
            RETURNING round_id INTO v_rid;

            FOR i IN 0..(m - 1) / 2 LOOP
                IF i = 0 THEN
                    w := fixed; b := rot[m];
                ELSE
                    w := rot[i]; b := rot[m - i];
                END IF;
                SELECT rating_classical INTO rw FROM player WHERE player_id = w;
                SELECT rating_classical INTO rb FROM player WHERE player_id = b;
                ww := (rw >= rb);   -- wygrywa wyżej notowany
                INSERT INTO game(round_id, white, black) VALUES (v_rid, w, b);
                INSERT INTO game_over(round_id, white, white_won, game_over_reason_id, arbiter_id)
                VALUES (v_rid, w, ww,
                        (SELECT game_over_reason_id FROM game_over_reason WHERE description = 'Szach Mat'),
                        p_main_arbiter);
                IF ww THEN
                    INSERT INTO round_rating(round_id, player_id, rating_change) VALUES (v_rid, w, 5), (v_rid, b, -5);
                ELSE
                    INSERT INTO round_rating(round_id, player_id, rating_change) VALUES (v_rid, w, -5), (v_rid, b, 5);
                END IF;
            END LOOP;

            rot := array_prepend(rot[m], rot[1:m - 1]);  -- rotacja kółkowa
        END LOOP;
    END IF;

    IF p_close THEN
        PERFORM fn_close_tournament(v_tid);
    END IF;

    RETURN v_tid;
END;
$$;

DO $$
BEGIN
    -- ZAKOŃCZONY: round-robin 6 osób, 5 rund, zamknięty
    PERFORM _seed_tournament(
        'Memoriał Akiby Rubinsteina', 1, 2, 5,
        '2025-03-01 09:00:00+01', '2025-03-10 20:00:00+01', 16, 'Marszałkowska 10',
        103, 103, ARRAY[115,116,117,118,119,120], ARRAY[104]::INT[],
        5, TRUE, TRUE);

    -- W TOKU: round-robin 4 osób, 3 rundy rozegrane, niezamknięty
    PERFORM _seed_tournament(
        'Mistrzostwa Pomorza', 2, 2, 3,
        '2025-04-05 10:00:00+02', '2025-04-12 20:00:00+02', 3, 'Długi Targ 1',
        116, 104, ARRAY[121,122,123,124], NULL,
        3, TRUE, FALSE);

    -- PUCHAR (drabinowy), rozegrana 1 runda
    PERFORM _seed_tournament(
        'Puchar Śląska', 5, 3, 3,
        '2025-05-10 09:00:00+02', '2025-05-15 20:00:00+02', 5, 'Korfantego 5',
        105, 105, ARRAY[125,126,127,128], NULL,
        1, TRUE, FALSE);

    -- ROZPOCZĘTY, bez rund
    PERFORM _seed_tournament(
        'Otwarte Mistrzostwa Krakowa', 1, 1, 7,
        '2025-06-01 09:00:00+02', '2025-06-30 20:00:00+02', 7, 'Rynek Główny 1',
        106, 106, ARRAY[129,130,131,132,133,134,135,136], ARRAY[107]::INT[],
        0, TRUE, FALSE);

    -- REJESTRACJA (nierozpoczęty)
    PERFORM _seed_tournament(
        'Turniej Noworoczny', 4, 1, 9,
        '2025-12-28 10:00:00+01', '2025-12-31 22:00:00+01', 17, 'Rynek 50',
        107, 107, ARRAY[137,138,139,140,141,142], NULL,
        0, FALSE, FALSE);

    -- REJESTRACJA (nierozpoczęty)
    PERFORM _seed_tournament(
        'Bałtycki Festiwal Szachowy', 3, 2, 5,
        '2025-07-15 10:00:00+02', '2025-07-20 20:00:00+02', 19, 'Skwer Kościuszki 15',
        108, 108, ARRAY[143,144,145,146,147,148,149,150], ARRAY[109,110]::INT[],
        0, FALSE, FALSE);

    -- ROZPOCZĘTY, bez rund
    PERFORM _seed_tournament(
        'Liga Juniorów', 1, 1, 5,
        '2025-09-01 09:00:00+02', '2025-09-10 20:00:00+02', 12, 'Św. Marcin 80',
        109, 109, ARRAY[151,152,153,154], NULL,
        0, TRUE, FALSE);

    -- REJESTRACJA (nierozpoczęty)
    PERFORM _seed_tournament(
        'Grand Prix Mazowsza', 6, 3, 3,
        '2025-10-01 09:00:00+02', '2025-10-05 20:00:00+02', 22, 'Żeromskiego 30',
        110, 110, ARRAY[155,156,157,158], NULL,
        0, FALSE, FALSE);

    -- ZAKOŃCZONY: round-robin 4 osób, 3 rundy, zamknięty
    PERFORM _seed_tournament(
        'Memoriał Tarrascha', 1, 2, 3,
        '2025-02-01 09:00:00+01', '2025-02-10 20:00:00+01', 9, 'Piotrkowska 100',
        111, 111, ARRAY[159,160,161,162], NULL,
        3, TRUE, TRUE);
END $$;

-- 20 dodatkowych turniejów (2023–2024), żeby zawodnicy mieli bogatą historię gry.
-- round-robin (kółkowy) lub mini-puchar; połowa zamknięta (rating_history).
DO $$
DECLARE
    flat INT[] := ARRAY[
        115,116,117,118, 119,120,121,122, 123,124,125,126, 127,128,129,130,
        131,132,133,134, 135,136,137,138, 139,140,141,142, 143,144,145,146,
        147,148,149,150, 151,152,153,154, 155,156,157,158, 159,160,161,162,
        115,119,123,127, 116,120,124,128, 117,121,125,129, 118,122,126,130,
        131,135,139,143, 132,136,140,144, 133,137,141,145, 134,138,142,146];
    names TEXT[] := ARRAY[
        'Festiwal Szachowy Cracovia', 'Memoriał Mieczysława Najdorfa', 'Otwarte Mistrzostwa Bałtyku',
        'Puchar Wisły', 'Grand Prix Tatr', 'Turniej Gwiazdkowy', 'Memoriał Dawida Przepiórki',
        'Mistrzostwa Wielkopolski', 'Festiwal nad Odrą', 'Puchar Bursztynowy',
        'Turniej Wiosenny w Sopocie', 'Memoriał Ksawerego Tartakowera', 'Otwarte Mistrzostwa Podhala',
        'Grand Prix Mazur', 'Puchar Niepodległości', 'Festiwal Szachowy Solidarność',
        'Memoriał Henryka Friedmana', 'Mistrzostwa Dolnego Śląska', 'Turniej Jesienny w Toruniu',
        'Puchar Smoka Wawelskiego'];
    i INT;
    v_sys INT; v_play INT; v_start TIMESTAMPTZ; v_end TIMESTAMPTZ;
BEGIN
    FOR i IN 1..20 LOOP
        v_sys  := CASE WHEN i % 5 = 0 THEN 3 WHEN i % 3 = 0 THEN 1 ELSE 2 END;  -- drabinowy / szwajcarski / kołowy
        v_play := CASE WHEN v_sys = 3 THEN 1 ELSE 3 END;                         -- puchar: 1 runda, reszta: 3
        v_start := '2023-01-03 10:00:00+01'::timestamptz + ((i - 1) || ' months')::interval;
        v_end   := v_start + interval '10 days';
        PERFORM _seed_tournament(
            names[i],
            1 + ((i - 1) % 7),          -- tempo 1..7
            v_sys, 3,
            v_start, v_end,
            1 + ((i - 1) % 40),         -- miasto 1..40
            'ul. Szachowa ' || i,
            103 + ((i - 1) % 12),       -- organizator
            103 + ((i - 1) % 12),       -- sędzia główny (musi być sędzią)
            flat[(i - 1) * 4 + 1 : i * 4],
            NULL,
            v_play, TRUE, (i % 2 = 0));  -- co drugi zamknięty
    END LOOP;
END $$;

-- Demonstracja pełnego awansu: Urbański (159, IM) wygrywa trzeci turniej z wynikiem > 2/3
-- => trzecia norma na GM => automatyczne przyznanie tytułu GM.
DO $$
BEGIN
    PERFORM _seed_tournament(
        'Turniej awansowy', 1, 2, 3,
        '2024-11-01 10:00:00+01', '2024-11-10 20:00:00+01', 7, 'Awansowa 1',
        103, 103, ARRAY[159, 136, 137, 138], NULL,
        3, TRUE, TRUE);
END $$;

DROP FUNCTION _seed_tournament(TEXT, INT, INT, INT, TIMESTAMPTZ, TIMESTAMPTZ, INT, TEXT, INT, INT, INT[], INT[], INT, BOOLEAN, BOOLEAN);

-- =========================================================================
-- KLASY ZAWODNICZE (tytuły i normy nadaje teraz automatycznie fn_close_tournament)
-- =========================================================================
INSERT INTO player_class_history(player_id, tournament_id, player_class_id) VALUES
(115, (SELECT tournament_id FROM tournament WHERE name = 'Memoriał Akiby Rubinsteina'), 1),
(116, (SELECT tournament_id FROM tournament WHERE name = 'Memoriał Akiby Rubinsteina'), 1),
(117, (SELECT tournament_id FROM tournament WHERE name = 'Memoriał Akiby Rubinsteina'), 2),
(159, (SELECT tournament_id FROM tournament WHERE name = 'Memoriał Tarrascha'), 1),
(160, (SELECT tournament_id FROM tournament WHERE name = 'Memoriał Tarrascha'), 2);

-- =========================================================================
-- KARY (kontekst zgodny z triggerem: osoba i sędzia muszą być w turnieju)
-- =========================================================================
INSERT INTO penalty(player_id, date_since, date_until, reason, tournament_id, arbiter_id, role_context_id) VALUES
(124, '2025-04-06', '2025-04-08', 'Spóźnienie na rundę',
    (SELECT tournament_id FROM tournament WHERE name = 'Mistrzostwa Pomorza'), 104,
    (SELECT penalty_role_context_id FROM penalty_role_context WHERE name = 'Zawodnik')),
(140, '2025-12-29', NULL, 'Niesportowe zachowanie',
    (SELECT tournament_id FROM tournament WHERE name = 'Turniej Noworoczny'), 107,
    (SELECT penalty_role_context_id FROM penalty_role_context WHERE name = 'Zawodnik'));

-- =========================================================================
-- KLUB DLA KAŻDEGO: gracze bez klubu zostają przypisani (z aktywnym członkostwem),
-- żeby każdy zawodnik należał do jakiegoś klubu (kluby 1..8, rozłożone równomiernie).
-- =========================================================================
DO $$
DECLARE
    pl INT;
    v_club INT;
BEGIN
    FOR pl IN SELECT player_id FROM player WHERE club_id IS NULL ORDER BY player_id LOOP
        v_club := ((pl - 1) % 8) + 1;   -- kluby 1..8
        INSERT INTO club_membership_history(player_id, club_id, date_since)
        VALUES (pl, v_club, '2023-01-01');
        UPDATE player SET club_id = v_club WHERE player_id = pl;
    END LOOP;
END $$;
