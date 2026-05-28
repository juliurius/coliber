package org.tcs.backend.db;

import org.tcs.backend.ArbiterClass;
import org.tcs.backend.Backend;
import org.tcs.backend.City;
import org.tcs.backend.Club;
import org.tcs.backend.ClubBrief;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerBrief;
import org.tcs.backend.PlayerClass;
import org.tcs.backend.Tempo;
import org.tcs.backend.Title;
import org.tcs.backend.Tournament;
import org.tcs.backend.TournamentBrief;
import org.tcs.backend.TournamentSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
    return CompletableFuture.supplyAsync(() -> {
      try {
        return query.run();
      } catch (SQLException e) {
        throw new RuntimeException("Database query failed", e);
      }
    });
  }

  @Override
  public CompletableFuture<Map<City.Id, City>> getCities() {
    return async(() -> {
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
          cities.put(id, new City(id, result.getString("name")));
        }

        return cities;
      }
    });
  }

  @Override
  public CompletableFuture<Map<Tempo.Id, Tempo>> getTempos() {
    return async(() -> {
      var sql =
          """
          SELECT tempo_id, name, description
          FROM tempo
          ORDER BY name
          """;

      try (
          var connection = connect();
          var statement = connection.prepareStatement(sql);
          var result = statement.executeQuery()
      ) {
        Map<Tempo.Id, Tempo> tempos = new LinkedHashMap<>();

        while (result.next()) {
          var id = new DbIds.TempoId(result.getInt("tempo_id"));
          tempos.put(id, new Tempo(id, result.getString("name"), result.getString("description")));
        }

        return tempos;
      }
    });
  }

  @Override
  public CompletableFuture<Map<TournamentSystem.Id, TournamentSystem>> getTournamentSystems() {
    return async(() -> {
      var sql =
          """
          SELECT tournament_system_id, name
          FROM tournament_system
          ORDER BY name
          """;

      try (
          var connection = connect();
          var statement = connection.prepareStatement(sql);
          var result = statement.executeQuery()
      ) {
        Map<TournamentSystem.Id, TournamentSystem> systems = new LinkedHashMap<>();

        while (result.next()) {
          var id = new DbIds.TournamentSystemId(result.getInt("tournament_system_id"));
          systems.put(id, new TournamentSystem(id, result.getString("name")));
        }

        return systems;
      }
    });
  }

  @Override
  public CompletableFuture<Map<PlayerClass.Id, PlayerClass>> getPlayerClasses() {
    return async(() -> {
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
          playerClasses.put(id, new PlayerClass(id, result.getString("name")));
        }

        return playerClasses;
      }
    });
  }

  @Override
  public CompletableFuture<Map<ArbiterClass.Id, ArbiterClass>> getArbiterClasses() {
    return CompletableFuture.completedFuture(Map.of());
  }

  @Override
  public CompletableFuture<Map<Title.Id, Title>> getTitles() {
    return CompletableFuture.completedFuture(Map.of());
  }

  @Override
  public CompletableFuture<List<TournamentBrief>> getTournaments() {
    return CompletableFuture.completedFuture(List.of());
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getPlayers() {
    return CompletableFuture.completedFuture(List.of());
  }

  @Override
  public CompletableFuture<List<ClubBrief>> getClubs() {
    return CompletableFuture.completedFuture(List.of());
  }

  @Override
  public CompletableFuture<Tournament> getTournament(Tournament.Id id) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Player> getPlayer(Player.Id id) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<Club> getClub(Club.Id id) {
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getClubMembers(Club.Id id) {
    return CompletableFuture.completedFuture(List.of());
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getTournamentArbiters(Tournament.Id id) {
    return CompletableFuture.completedFuture(List.of());
  }
}
