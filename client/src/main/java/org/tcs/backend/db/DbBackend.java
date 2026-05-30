package org.tcs.backend.db;

import org.tcs.backend.ArbiterClass;
import org.tcs.backend.Backend;
import org.tcs.backend.City;
import org.tcs.backend.Club;
import org.tcs.backend.ClubBrief;
import org.tcs.backend.Game;
import org.tcs.backend.GameOverReason;
import org.tcs.backend.Norm;
import org.tcs.backend.Penalty;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerBrief;
import org.tcs.backend.PlayerClass;
import org.tcs.backend.Round;
import org.tcs.backend.Tempo;
import org.tcs.backend.Title;
import org.tcs.backend.Tournament;
import org.tcs.backend.TournamentBrief;
import org.tcs.backend.TournamentSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DbBackend implements Backend {
  private final DbConfig config;

  public DbBackend(DbConfig config) {
    this.config = config;
  }

  private Connection connect() throws SQLException {
    return DriverManager.getConnection(config.url(), config.user(), config.password());
  }

  private interface DbQuery<T> {
    T run() throws SQLException;
  }

  private interface DbWrite {
    void run() throws SQLException;
  }

  private static <T> CompletableFuture<T> async(DbQuery<T> query) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            return query.run();
          } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
          }
        });
  }

  private static CompletableFuture<String> asyncWrite(DbWrite write) {
    return CompletableFuture.supplyAsync(
        () -> {
          try {
            write.run();
            return null;
          } catch (SQLException e) {
            return e.getMessage();
          }
        });
  }

  private static int value(Player.Id id) {
    if (id instanceof DbIds.PlayerId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database Player.Id");
  }

  private static int value(City.Id id) {
    if (id instanceof DbIds.CityId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database City.Id");
  }

  private static int value(Club.Id id) {
    if (id instanceof DbIds.ClubId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database Club.Id");
  }

  private static int value(Tournament.Id id) {
    if (id instanceof DbIds.TournamentId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database Tournament.Id");
  }

  private static int value(Round.Id id) {
    if (id instanceof DbIds.RoundId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database Round.Id");
  }

  private static int value(PlayerClass.Id id) {
    if (id instanceof DbIds.PlayerClassId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database PlayerClass.Id");
  }

  private static int value(ArbiterClass.Id id) {
    if (id instanceof DbIds.ArbiterClassId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database ArbiterClass.Id");
  }

  private static int value(Title.Id id) {
    if (id instanceof DbIds.TitleId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database Title.Id");
  }

  private static City.Id nullableCityId(ResultSet result, String column) throws SQLException {
    int value = result.getInt(column);

    if (result.wasNull()) {
      return null;
    }

    return new DbIds.CityId(value);
  }

  private static PlayerBrief playerBrief(ResultSet result) throws SQLException {
    return new PlayerBrief(
        new DbIds.PlayerId(result.getInt("player_id")),
        result.getString("name"),
        result.getString("surname"),
        result.getInt("rating"));
  }

  private static PlayerBrief playerBrief(ResultSet result, String prefix) throws SQLException {
    return new PlayerBrief(
        new DbIds.PlayerId(result.getInt(prefix + "_id")),
        result.getString(prefix + "_name"),
        result.getString(prefix + "_surname"),
        result.getInt(prefix + "_rating"));
  }

  private static PlayerBrief nullablePlayerBrief(ResultSet result, String prefix) throws SQLException {
    int id = result.getInt(prefix + "_id");

    if (result.wasNull()) {
      return null;
    }

    return new PlayerBrief(
        new DbIds.PlayerId(id),
        result.getString(prefix + "_name"),
        result.getString(prefix + "_surname"),
        result.getInt(prefix + "_rating"));
  }

  private static ClubBrief nullableClubBrief(ResultSet result) throws SQLException {
    int id = result.getInt("club_id");

    if (result.wasNull()) {
      return null;
    }

    return new ClubBrief(
        new DbIds.ClubId(id),
        result.getString("club_name"),
        nullableCityId(result, "club_city_id"));
  }

  private static int tempoId(Connection connection, Tempo tempo) throws SQLException {
    var sql =
        """
        SELECT tempo_id
        FROM tempo
        WHERE name IS NOT DISTINCT FROM ?
          AND description IS NOT DISTINCT FROM ?
        """;

    try (
        var statement = connection.prepareStatement(sql)
    ) {
      statement.setString(1, tempo.name());
      statement.setString(2, tempo.description());

      var result = statement.executeQuery();

      if (!result.next()) {
        throw new SQLException("Tempo does not exist");
      }

      return result.getInt("tempo_id");
    }
  }

  private static int tournamentSystemId(
      Connection connection,
      TournamentSystem system) throws SQLException {
    var sql =
        """
        SELECT tournament_system_id
        FROM tournament_system
        WHERE name = ?
        """;

    try (
        var statement = connection.prepareStatement(sql)
    ) {
      statement.setString(1, system.name());

      var result = statement.executeQuery();

      if (!result.next()) {
        throw new SQLException("Tournament system does not exist");
      }

      return result.getInt("tournament_system_id");
    }
  }

  private static PlayerClass.Id latestPlayerClass(Connection connection, int playerId) throws SQLException {
    var sql =
        """
        SELECT pch.player_class_id
        FROM player_class_history pch
        JOIN tournament t ON t.tournament_id = pch.tournament_id
        WHERE pch.player_id = ?
        ORDER BY t.time_end DESC
        LIMIT 1
        """;

    try (
        var statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, playerId);

      var result = statement.executeQuery();

      if (!result.next()) {
        return null;
      }

      return new DbIds.PlayerClassId(result.getInt("player_class_id"));
    }
  }

  private static ArbiterClass.Id latestArbiterClass(Connection connection, int playerId) throws SQLException {
    var sql =
        """
        SELECT arbiter_class_id
        FROM arbiter_class_history
        WHERE arbiter_id = ?
        ORDER BY date_since DESC
        LIMIT 1
        """;

    try (
        var statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, playerId);

      var result = statement.executeQuery();

      if (!result.next()) {
        return null;
      }

      return new DbIds.ArbiterClassId(result.getInt("arbiter_class_id"));
    }
  }

  private static Title.Id latestTitle(Connection connection, int playerId) throws SQLException {
    var sql =
        """
        SELECT th.title_id
        FROM title_history th
        JOIN tournament t ON t.tournament_id = th.tournament_id
        WHERE th.player_id = ?
        ORDER BY t.time_end DESC
        LIMIT 1
        """;

    try (
        var statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, playerId);

      var result = statement.executeQuery();

      if (!result.next()) {
        return null;
      }

      return new DbIds.TitleId(result.getInt("title_id"));
    }
  }

  @Override
  public CompletableFuture<Map<City.Id, City>> getCities() {
    return async(
        () -> {
          var sql =
              """
              SELECT city_id, name
              FROM city
              ORDER BY name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            Map<City.Id, City> cities = new LinkedHashMap<>();

            while (result.next()) {
              var id = new DbIds.CityId(result.getInt("city_id"));
              var city = new City(id, result.getString("name"));

              cities.put(id, city);
            }

            return cities;
          }
        });
  }

  @Override
  public CompletableFuture<List<Tempo>> getTempos() {
    return async(
        () -> {
          var sql =
              """
              SELECT name, description
              FROM tempo
              ORDER BY name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            List<Tempo> tempos = new ArrayList<>();

            while (result.next()) {
              var tempo =
                  new Tempo(
                      result.getString("name"),
                      result.getString("description"));

              tempos.add(tempo);
            }

            return tempos;
          }
        });
  }

  @Override
  public CompletableFuture<List<TournamentSystem>> getTournamentSystems() {
    return async(
        () -> {
          var sql =
              """
              SELECT name
              FROM tournament_system
              ORDER BY name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            List<TournamentSystem> systems = new ArrayList<>();

            while (result.next()) {
              var system = new TournamentSystem(result.getString("name"));

              systems.add(system);
            }

            return systems;
          }
        });
  }

  @Override
  public CompletableFuture<Map<PlayerClass.Id, PlayerClass>> getPlayerClasses() {
    return async(
        () -> {
          var sql =
              """
              SELECT player_class_id, name
              FROM player_class
              ORDER BY name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            Map<PlayerClass.Id, PlayerClass> playerClasses = new LinkedHashMap<>();

            while (result.next()) {
              var id = new DbIds.PlayerClassId(result.getInt("player_class_id"));
              var playerClass = new PlayerClass(id, result.getString("name"));

              playerClasses.put(id, playerClass);
            }

            return playerClasses;
          }
        });
  }

  @Override
  public CompletableFuture<Map<ArbiterClass.Id, ArbiterClass>> getArbiterClasses() {
    return async(
        () -> {
          var sql =
              """
              SELECT arbiter_class_id, name
              FROM arbiter_class
              ORDER BY name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            Map<ArbiterClass.Id, ArbiterClass> arbiterClasses = new LinkedHashMap<>();

            while (result.next()) {
              var id = new DbIds.ArbiterClassId(result.getInt("arbiter_class_id"));
              var arbiterClass = new ArbiterClass(id, result.getString("name"));

              arbiterClasses.put(id, arbiterClass);
            }

            return arbiterClasses;
          }
        });
  }

  @Override
  public CompletableFuture<Map<Title.Id, Title>> getTitles() {
    return async(
        () -> {
          var sql =
              """
              SELECT title_id, name
              FROM title
              ORDER BY name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            Map<Title.Id, Title> titles = new LinkedHashMap<>();

            while (result.next()) {
              var id = new DbIds.TitleId(result.getInt("title_id"));
              var title = new Title(id, result.getString("name"));

              titles.put(id, title);
            }

            return titles;
          }
        });
  }

  @Override
  public CompletableFuture<List<TournamentBrief>> getTournaments() {
    return async(
        () -> {
          var sql =
              """
              SELECT tournament_id, name, time_start, time_end, city_id
              FROM tournament
              ORDER BY time_start, name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            List<TournamentBrief> tournaments = new ArrayList<>();

            while (result.next()) {
              var tournament =
                  new TournamentBrief(
                      new DbIds.TournamentId(result.getInt("tournament_id")),
                      result.getString("name"),
                      result.getTimestamp("time_start"),
                      result.getTimestamp("time_end"),
                      nullableCityId(result, "city_id"));

              tournaments.add(tournament);
            }

            return tournaments;
          }
        });
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getPlayers() {
    return async(
        () -> {
          var sql =
              """
              SELECT player_id, name, surname, rating
              FROM player
              ORDER BY rating DESC, surname, name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            List<PlayerBrief> players = new ArrayList<>();

            while (result.next()) {
              players.add(playerBrief(result));
            }

            return players;
          }
        });
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getArbiters() {
    return async(
        () -> {
          var sql =
              """
              SELECT p.player_id, p.name, p.surname, p.rating
              FROM player p
              WHERE EXISTS (
                SELECT 1
                FROM arbiter_class_history ach
                WHERE ach.arbiter_id = p.player_id
              )
              ORDER BY p.surname, p.name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            List<PlayerBrief> arbiters = new ArrayList<>();

            while (result.next()) {
              arbiters.add(playerBrief(result));
            }

            return arbiters;
          }
        });
  }

  @Override
  public CompletableFuture<List<ClubBrief>> getClubs() {
    return async(
        () -> {
          var sql =
              """
              SELECT club_id, name, city_id
              FROM club
              ORDER BY name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            List<ClubBrief> clubs = new ArrayList<>();

            while (result.next()) {
              var club =
                  new ClubBrief(
                      new DbIds.ClubId(result.getInt("club_id")),
                      result.getString("name"),
                      nullableCityId(result, "city_id"));

              clubs.add(club);
            }

            return clubs;
          }
        });
  }

  @Override
  public CompletableFuture<Tournament.Id> createTournament(
      String name,
      Timestamp start,
      Timestamp end,
      City.Id city,
      String address,
      Tempo tempo,
      TournamentSystem system,
      Player.Id organiser,
      Player.Id mainArbiter) {
    return async(
        () -> {
          var tournamentSql =
              """
              INSERT INTO tournament(
                name,
                time_start,
                time_end,
                city_id,
                address,
                tempo_id,
                system_id,
                organiser,
                main_arbiter
              )
              VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
              RETURNING tournament_id
              """;

          var arbiterSql =
              """
              INSERT INTO tournament_arbiter(tournament_id, arbiter_id)
              VALUES (?, ?)
              """;

          try (
              var connection = connect()
          ) {
            connection.setAutoCommit(false);

            try (
                var tournamentStatement = connection.prepareStatement(tournamentSql);
                var arbiterStatement = connection.prepareStatement(arbiterSql)
            ) {
              int tempoId = tempoId(connection, tempo);
              int systemId = tournamentSystemId(connection, system);

              tournamentStatement.setString(1, name);
              tournamentStatement.setTimestamp(2, start);
              tournamentStatement.setTimestamp(3, end);

              if (city == null) {
                tournamentStatement.setNull(4, Types.INTEGER);
              } else {
                tournamentStatement.setInt(4, value(city));
              }

              tournamentStatement.setString(5, address);
              tournamentStatement.setInt(6, tempoId);
              tournamentStatement.setInt(7, systemId);
              tournamentStatement.setInt(8, value(organiser));
              tournamentStatement.setInt(9, value(mainArbiter));

              int tournamentId;

              try (
                  var result = tournamentStatement.executeQuery()
              ) {
                if (!result.next()) {
                  throw new SQLException("Tournament insert returned no id");
                }

                tournamentId = result.getInt("tournament_id");
              }

              arbiterStatement.setInt(1, tournamentId);
              arbiterStatement.setInt(2, value(mainArbiter));
              arbiterStatement.executeUpdate();

              connection.commit();

              return new DbIds.TournamentId(tournamentId);
            } catch (SQLException e) {
              connection.rollback();
              throw e;
            }
          }
        });
  }

  @Override
  public CompletableFuture<Tournament> getTournament(Tournament.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT
                t.tournament_id,
                t.name,
                t.time_start,
                t.time_end,
                t.city_id,
                t.address,
                tempo.name AS tempo_name,
                tempo.description AS tempo_description,
                system.name AS system_name,
                organiser.player_id AS organiser_id,
                organiser.name AS organiser_name,
                organiser.surname AS organiser_surname,
                organiser.rating AS organiser_rating,
                main_arbiter.player_id AS main_arbiter_id,
                main_arbiter.name AS main_arbiter_name,
                main_arbiter.surname AS main_arbiter_surname,
                main_arbiter.rating AS main_arbiter_rating
              FROM tournament t
              JOIN tempo ON tempo.tempo_id = t.tempo_id
              JOIN tournament_system system ON system.tournament_system_id = t.system_id
              JOIN player organiser ON organiser.player_id = t.organiser
              JOIN player main_arbiter ON main_arbiter.player_id = t.main_arbiter
              WHERE t.tournament_id = ?
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();

            if (!result.next()) {
              return null;
            }

            return new Tournament(
                new DbIds.TournamentId(result.getInt("tournament_id")),
                result.getString("name"),
                result.getTimestamp("time_start"),
                result.getTimestamp("time_end"),
                nullableCityId(result, "city_id"),
                result.getString("address"),
                new Tempo(
                    result.getString("tempo_name"),
                    result.getString("tempo_description")),
                new TournamentSystem(result.getString("system_name")),
                playerBrief(result, "organiser"),
                playerBrief(result, "main_arbiter"));
          }
        });
  }

  @Override
  public CompletableFuture<Player> getPlayer(Player.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT
                p.player_id,
                p.name,
                p.surname,
                p.rating,
                c.club_id,
                c.name AS club_name,
                c.city_id AS club_city_id
              FROM player p
              LEFT JOIN club c ON c.club_id = p.club_id
              WHERE p.player_id = ?
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            int playerId = value(id);

            statement.setInt(1, playerId);

            var result = statement.executeQuery();

            if (!result.next()) {
              return null;
            }

            return new Player(
                new DbIds.PlayerId(result.getInt("player_id")),
                result.getString("name"),
                result.getString("surname"),
                result.getInt("rating"),
                nullableClubBrief(result),
                latestPlayerClass(connection, playerId),
                latestArbiterClass(connection, playerId),
                latestTitle(connection, playerId));
          }
        });
  }

  @Override
  public CompletableFuture<Club> getClub(Club.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT
                c.club_id,
                c.name,
                c.city_id,
                president.player_id AS president_id,
                president.name AS president_name,
                president.surname AS president_surname,
                president.rating AS president_rating
              FROM club c
              LEFT JOIN player president ON president.player_id = c.president
              WHERE c.club_id = ?
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();

            if (!result.next()) {
              return null;
            }

            return new Club(
                new DbIds.ClubId(result.getInt("club_id")),
                result.getString("name"),
                nullableCityId(result, "city_id"),
                nullablePlayerBrief(result, "president"));
          }
        });
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getClubMembers(Club.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT player_id, name, surname, rating
              FROM player
              WHERE club_id = ?
              ORDER BY rating DESC, surname, name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();
            List<PlayerBrief> players = new ArrayList<>();

            while (result.next()) {
              players.add(playerBrief(result));
            }

            return players;
          }
        });
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getTournamentArbiters(Tournament.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT p.player_id, p.name, p.surname, p.rating
              FROM tournament_arbiter ta
              JOIN player p ON p.player_id = ta.arbiter_id
              WHERE ta.tournament_id = ?
              ORDER BY p.surname, p.name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();
            List<PlayerBrief> arbiters = new ArrayList<>();

            while (result.next()) {
              arbiters.add(playerBrief(result));
            }

            return arbiters;
          }
        });
  }

  @Override
  public CompletableFuture<Map<GameOverReason.Id, GameOverReason>> getGameOverReasons() {
    return async(
        () -> {
          var sql =
              """
              SELECT game_over_reason_id, description, win_score, lose_score
              FROM game_over_reason
              ORDER BY game_over_reason_id
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()
          ) {
            Map<GameOverReason.Id, GameOverReason> reasons = new LinkedHashMap<>();

            while (result.next()) {
              var id = new DbIds.GameOverReasonId(result.getInt("game_over_reason_id"));
              var reason =
                  new GameOverReason(
                      id,
                      result.getString("description"),
                      result.getFloat("win_score"),
                      result.getFloat("lose_score"));

              reasons.put(id, reason);
            }

            return reasons;
          }
        });
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getTournamentPlayers(Tournament.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT p.player_id, p.name, p.surname, p.rating
              FROM tournament_player tp
              JOIN player p ON p.player_id = tp.player_id
              WHERE tp.tournament_id = ?
              ORDER BY tp.score DESC, p.rating DESC, p.surname, p.name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();
            List<PlayerBrief> players = new ArrayList<>();

            while (result.next()) {
              players.add(playerBrief(result));
            }

            return players;
          }
        });
  }

  @Override
  public CompletableFuture<List<Round>> getTournamentRounds(Tournament.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT round_id
              FROM round
              WHERE tournament_id = ?
              ORDER BY time_start, round_id
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();
            List<Round> rounds = new ArrayList<>();

            while (result.next()) {
              rounds.add(new Round(new DbIds.RoundId(result.getInt("round_id"))));
            }

            return rounds;
          }
        });
  }

  @Override
  public CompletableFuture<List<Game>> getRoundGames(Round.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT
                white.player_id AS white_id,
                white.name AS white_name,
                white.surname AS white_surname,
                white.rating AS white_rating,
                black.player_id AS black_id,
                black.name AS black_name,
                black.surname AS black_surname,
                black.rating AS black_rating,
                go.white_won,
                go.game_over_reason_id,
                white_rating.rating_change AS white_rating_change,
                black_rating.rating_change AS black_rating_change,
                arbiter.player_id AS arbiter_id,
                arbiter.name AS arbiter_name,
                arbiter.surname AS arbiter_surname,
                arbiter.rating AS arbiter_rating
              FROM game g
              JOIN player white ON white.player_id = g.white
              JOIN player black ON black.player_id = g.black
              LEFT JOIN game_over go ON go.round_id = g.round_id AND go.white = g.white
              LEFT JOIN round_rating white_rating
                ON white_rating.round_id = g.round_id AND white_rating.player_id = g.white
              LEFT JOIN round_rating black_rating
                ON black_rating.round_id = g.round_id AND black_rating.player_id = g.black
              LEFT JOIN player arbiter ON arbiter.player_id = go.arbiter_id
              WHERE g.round_id = ?
              ORDER BY white.surname, white.name, black.surname, black.name
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();
            List<Game> games = new ArrayList<>();

            while (result.next()) {
              var reasonId = result.getInt("game_over_reason_id");
              Game.Over over = null;

              if (!result.wasNull()) {
                over =
                    new Game.Over(
                        result.getBoolean("white_won"),
                        result.getInt("white_rating_change"),
                        result.getInt("black_rating_change"),
                        new DbIds.GameOverReasonId(reasonId),
                        playerBrief(result, "arbiter"));
              }

              games.add(new Game(playerBrief(result, "white"), playerBrief(result, "black"), over));
            }

            return games;
          }
        });
  }

  @Override
  public CompletableFuture<List<Penalty>> getPlayerPenalties(Player.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT
                p.date_until,
                p.reason,
                p.tournament_id,
                arbiter.player_id AS arbiter_id,
                arbiter.name AS arbiter_name,
                arbiter.surname AS arbiter_surname,
                arbiter.rating AS arbiter_rating
              FROM penalty p
              JOIN player arbiter ON arbiter.player_id = p.arbiter_id
              WHERE p.player_id = ?
              ORDER BY p.date_since DESC
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();
            List<Penalty> penalties = new ArrayList<>();

            while (result.next()) {
              penalties.add(
                  new Penalty(
                      result.getDate("date_until"),
                      result.getString("reason"),
                      new DbIds.TournamentId(result.getInt("tournament_id")),
                      playerBrief(result, "arbiter")));
            }

            return penalties;
          }
        });
  }

  @Override
  public CompletableFuture<List<Norm>> getPlayerNorms(Player.Id id) {
    return async(
        () -> {
          var sql =
              """
              SELECT
                t.tournament_id,
                t.name,
                t.time_start,
                t.time_end,
                t.city_id,
                n.title_id
              FROM norm n
              JOIN tournament t ON t.tournament_id = n.tournament_id
              WHERE n.player_id = ?
              ORDER BY n.date_until, t.time_start
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(id));

            var result = statement.executeQuery();
            List<Norm> norms = new ArrayList<>();

            while (result.next()) {
              var tournament =
                  new TournamentBrief(
                      new DbIds.TournamentId(result.getInt("tournament_id")),
                      result.getString("name"),
                      result.getTimestamp("time_start"),
                      result.getTimestamp("time_end"),
                      nullableCityId(result, "city_id"));

              norms.add(new Norm(tournament, new DbIds.TitleId(result.getInt("title_id"))));
            }

            return norms;
          }
        });
  }

  @Override
  public CompletableFuture<String> createPlayer(Player.Data data) {
    return asyncWrite(
        () -> {
          var sql =
              """
              INSERT INTO player(name, surname)
              VALUES (?, ?)
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setString(1, data.name());
            statement.setString(2, data.surname());
            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> createTempo(Tempo.Data data) {
    return asyncWrite(
        () -> {
          var sql =
              """
              INSERT INTO tempo(name, description)
              VALUES (?, ?)
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setString(1, data.name());
            statement.setString(2, data.description());
            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> createTournamentSystem(TournamentSystem.Data data) {
    return asyncWrite(
        () -> {
          var sql =
              """
              INSERT INTO tournament_system(name)
              VALUES (?)
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setString(1, data.name());
            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> createClub(Club.Data data) {
    return asyncWrite(
        () -> {
          var sql =
              """
              INSERT INTO club(name, city_id)
              VALUES (?, ?)
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setString(1, data.name());

            if (data.city() == null) {
              statement.setNull(2, Types.INTEGER);
            } else {
              statement.setInt(2, value(data.city()));
            }

            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> addClubMember(Club.Id clubId, Player.Id playerId) {
    return asyncWrite(
        () -> {
          if (playerId == null) {
            throw new SQLException("Player is required");
          }

          var sql =
              """
              UPDATE player
              SET club_id = ?
              WHERE player_id = ?
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(clubId));
            statement.setInt(2, value(playerId));
            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> setClubPresident(Club.Id clubId, Player.Id playerId) {
    return asyncWrite(
        () -> {
          var sql =
              """
              UPDATE club
              SET president = ?
              WHERE club_id = ?
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            if (playerId == null) {
              statement.setNull(1, Types.INTEGER);
            } else {
              statement.setInt(1, value(playerId));
            }

            statement.setInt(2, value(clubId));
            statement.executeUpdate();
          }
        });
  }

  @Override
  public void removeClubMember(Player.Id playerId) {
    asyncWrite(
        () -> {
          var sql =
              """
              UPDATE player
              SET club_id = NULL
              WHERE player_id = ?
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(playerId));
            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> setPlayerArbiterClass(Player.Id playerId, ArbiterClass.Id arbiterClass) {
    return asyncWrite(
        () -> {
          if (arbiterClass == null) {
            throw new SQLException("Arbiter class is required");
          }

          var sql =
              """
              INSERT INTO arbiter_class_history(arbiter_id, date_since, arbiter_class_id)
              VALUES (?, CURRENT_DATE, ?)
              ON CONFLICT (arbiter_id, date_since)
              DO UPDATE SET arbiter_class_id = EXCLUDED.arbiter_class_id
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(playerId));
            statement.setInt(2, value(arbiterClass));
            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> setPlayerPlayerClass(Player.Id playerId, PlayerClass.Id playerClass, Tournament.Id tournament) {
    return asyncWrite(
        () -> {
          if (playerClass == null) {
            throw new SQLException("Player class is required");
          }

          if (tournament == null) {
            throw new SQLException("Tournament is required");
          }

          var sql =
              """
              INSERT INTO player_class_history(player_id, tournament_id, player_class_id)
              VALUES (?, ?, ?)
              ON CONFLICT (player_id, tournament_id)
              DO UPDATE SET player_class_id = EXCLUDED.player_class_id
              """;

          try (
              var connection = connect();
              var statement = connection.prepareStatement(sql)
          ) {
            statement.setInt(1, value(playerId));
            statement.setInt(2, value(tournament));
            statement.setInt(3, value(playerClass));
            statement.executeUpdate();
          }
        });
  }

  @Override
  public CompletableFuture<String> setPlayerTitle(Player.Id playerId, Title.Id title, Tournament.Id tournament) {
    return asyncWrite(
        () -> {
          if (title == null) {
            throw new SQLException("Title is required");
          }

          if (tournament == null) {
            throw new SQLException("Tournament is required");
          }

          var deleteSql =
              """
              DELETE FROM title_history
              WHERE player_id = ?
                AND tournament_id = ?
              """;

          var insertSql =
              """
              INSERT INTO title_history(player_id, title_id, tournament_id)
              VALUES (?, ?, ?)
              """;

          try (
              var connection = connect()
          ) {
            connection.setAutoCommit(false);

            try (
                var deleteStatement = connection.prepareStatement(deleteSql);
                var insertStatement = connection.prepareStatement(insertSql)
            ) {
              deleteStatement.setInt(1, value(playerId));
              deleteStatement.setInt(2, value(tournament));
              deleteStatement.executeUpdate();

              insertStatement.setInt(1, value(playerId));
              insertStatement.setInt(2, value(title));
              insertStatement.setInt(3, value(tournament));
              insertStatement.executeUpdate();

              connection.commit();
            } catch (SQLException e) {
              connection.rollback();
              throw e;
            }
          }
        });
  }

  public CompletableFuture<String> addTournamentPlayer(Tournament.Id tournamentId, Player.Id playerId) {
    return asyncWrite(
      () -> {
        if (playerId == null) throw new SQLException("Player is required");

        var sql =
                """
                INSERT INTO tournament_player(score, tournament_id, player_id)
                VALUES(0, ?, ?)
                """;
        try (
          var connection = connect();
        ) {
          try (
            var insertStatement = connection.prepareStatement(sql);
          ) {
            insertStatement.setInt(1, value(tournamentId));
            insertStatement.setInt(2, value(playerId));
            insertStatement.executeUpdate();
          }
        }
      }
    );
  }

  public CompletableFuture<String> addTournamentArbiter(Tournament.Id tournamentId, Player.Id playerId) {
    return asyncWrite(
            () -> {
              if (playerId == null) throw new SQLException("Player is required");

              var sql =
                      """
                      INSERT INTO tournament_arbiter(tournament_id, arbiter_id)
                      VALUES(?, ?)
                      """;
              try (
                      var connection = connect();
              ) {
                try (
                        var insertStatement = connection.prepareStatement(sql);
                ) {
                  insertStatement.setInt(1, value(tournamentId));
                  insertStatement.setInt(2, value(playerId));
                  insertStatement.executeUpdate();
                }
              }
            }
    );
  }
}