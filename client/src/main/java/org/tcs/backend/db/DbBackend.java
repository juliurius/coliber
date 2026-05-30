package org.tcs.backend.db;

import org.tcs.backend.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
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

  private static int value(Player.Id id) {
    if (id instanceof DbIds.PlayerId dbId) {
      return dbId.value();
    }

    throw new IllegalArgumentException("Expected database Player.Id");
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
              SELECT tempo_id, name, description
              FROM tempo
              ORDER BY name
              """;

          try (var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()) {
            List<Tempo> tempos = new ArrayList<>();

            while (result.next()) {
              tempos.add(new Tempo(result.getString("name"), result.getString("description")));
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
              SELECT tournament_system_id, name
              FROM tournament_system
              ORDER BY name
              """;

          try (var connection = connect();
              var statement = connection.prepareStatement(sql);
              var result = statement.executeQuery()) {
            List<TournamentSystem> systems = new ArrayList<>();

            while (result.next()) {
              systems.add(new TournamentSystem(result.getString("name")));
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
  public CompletableFuture<Map<GameOverReason.Id, GameOverReason>> getGameOverReasons() {
    throw new UnsupportedOperationException("Not implemented");
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
                tempo.name as tempo_name,
                tempo.description as tempo_description,
                ts.name as system_name,
                organiser.player_id AS organiser_id,
                organiser.name AS organiser_name,
                organiser.surname AS organiser_surname,
                organiser.rating AS organiser_rating,
                main_arbiter.player_id AS main_arbiter_id,
                main_arbiter.name AS main_arbiter_name,
                main_arbiter.surname AS main_arbiter_surname,
                main_arbiter.rating AS main_arbiter_rating
              FROM tournament t
              JOIN player organiser ON organiser.player_id = t.organiser
              JOIN player main_arbiter ON main_arbiter.player_id = t.main_arbiter
              JOIN tempo ON tempo.tempo_id = t.tempo_id
              JOIN tournament_system ts ON ts.tournament_system_id = t.system_id
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
                new Tempo(result.getString("tempo_name"), result.getString("tempo_description")),
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
  public CompletableFuture<List<PlayerBrief>> getTournamentPlayers(Tournament.Id id) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<List<Round>> getTournamentRounds(Tournament.Id id) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<List<Game>> getRoundGames(Round.Id id) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<List<Penalty>> getPlayerPenalties(Player.Id id) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<List<Norm>> getPlayerNorms(Player.Id id) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> createPlayer(Player.Data data) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> createTempo(Tempo.Data data) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> createTournamentSystem(TournamentSystem.Data data) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> createClub(Club.Data data) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> addClubMember(Club.Id clubId, Player.Id playerId) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> setClubPresident(Club.Id clubId, Player.Id playerId) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public void removeClubMember(Player.Id playerId) {

  }

  @Override
  public CompletableFuture<String> setPlayerArbiterClass(Player.Id playerId, ArbiterClass.Id arbiterClass) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> setPlayerPlayerClass(Player.Id playerId, PlayerClass.Id playerClass, Tournament.Id tournament) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> setPlayerTitle(Player.Id playerId, Title.Id title, Tournament.Id tournament) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public CompletableFuture<String> addPlayerPenalty(Player.Id playerId, Penalty.Data data) {
    throw new UnsupportedOperationException("Not implemented");
  }
}
