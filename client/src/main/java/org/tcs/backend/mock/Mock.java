package org.tcs.backend.mock;

import javafx.util.Pair;
import org.tcs.backend.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Mock implements Backend {
  private static final Timestamp epoch = Timestamp.from(Instant.EPOCH);

  record FakeId(int id)
      implements City.Id, Tournament.Id, Tempo.Id, TournamentSystem.Id, Player.Id, Club.Id, PlayerClass.Id, ArbiterClass.Id {}

  final List<City> cities =
      List.of(new City(new FakeId(0), "Kraków"), new City(new FakeId(1), "Opole"));
  final List<Tempo> tempos =
      List.of(new Tempo(new FakeId(0), "Blitz", "3|2"), new Tempo(new FakeId(1), "Bullet", "1|0"));
  final List<TournamentSystem> systems = List.of(new TournamentSystem(new FakeId(0), "Swiss"));
  final List<PlayerClass> playerClasses = List.of(new PlayerClass(new FakeId(0), "Good"), new PlayerClass(new FakeId(1), "Bad"));
  final List<ArbiterClass> arbiterClasses = List.of(new ArbiterClass(new FakeId(0), "Pretty"), new ArbiterClass(new FakeId(1), "Ugly"));
  final Map<Player.Id, Player> players =
      Map.of(
          new FakeId(0),
          new Player(
              new FakeId(0),
              "Magnus",
              "Carlsen",
              2882,
              new ClubBrief(new FakeId(0), "Szachiści z Opola", new FakeId(1))),
          new FakeId(1),
          new Player(
              new FakeId(1),
              "Carlos",
              "Magnussen",
              9001,
              new ClubBrief(new FakeId(1), "Wisła Kraków", new FakeId(0))));
  final Map<Club.Id, Club> clubs =
      Map.of(
          new FakeId(0),
          new Club(
              new FakeId(0),
              "Szachiści z Opola",
              new FakeId(1),
              players.get(new FakeId(0)).getBrief()),
          new FakeId(1),
          new Club(new FakeId(1), "Wisła Kraków", new FakeId(0), null));
  final Map<Tournament.Id, Tournament> tournaments =
      Map.of(
          new FakeId(0),
          new Tournament(
              new FakeId(0),
              "Test 1",
              epoch,
              epoch,
              new FakeId(0),
              null,
              new FakeId(1),
              new FakeId(0),
              players.get(new FakeId(0)).getBrief(),
              players.get(new FakeId(0)).getBrief()),
          new FakeId(1),
          new Tournament(
              new FakeId(1),
              "Test 2",
              epoch,
              epoch,
              new FakeId(1),
              "ul. Łojasiewicza 6",
              new FakeId(1),
              new FakeId(0),
              players.get(new FakeId(1)).getBrief(),
              players.get(new FakeId(1)).getBrief()));
  final Set<Pair<Tournament.Id, Player.Id>> arbiters =
      Set.of(new Pair<>(new FakeId(0), new FakeId(0)), new Pair<>(new FakeId(1), new FakeId(1)));

  @Override
  public CompletableFuture<Map<City.Id, City>> getCities() {
    return CompletableFuture.completedFuture(
        cities.stream().collect(Collectors.toMap(City::id, v -> v)));
  }

  @Override
  public CompletableFuture<Map<Tempo.Id, Tempo>> getTempos() {
    return CompletableFuture.completedFuture(
        tempos.stream().collect(Collectors.toMap(Tempo::id, v -> v)));
  }

  @Override
  public CompletableFuture<Map<TournamentSystem.Id, TournamentSystem>> getTournamentSystems() {
    return CompletableFuture.completedFuture(
        systems.stream().collect(Collectors.toMap(TournamentSystem::id, v -> v)));
  }

  @Override
  public CompletableFuture<Map<PlayerClass.Id, PlayerClass>> getPlayerClasses() {
    return CompletableFuture.completedFuture(
        playerClasses.stream().collect(Collectors.toMap(PlayerClass::id, v -> v)));
  }

  @Override
  public CompletableFuture<Map<ArbiterClass.Id, ArbiterClass>> getArbiterClasses() {
    return CompletableFuture.completedFuture(
        arbiterClasses.stream().collect(Collectors.toMap(ArbiterClass::id, v -> v)));
  }

  @Override
  public CompletableFuture<List<TournamentBrief>> getTournaments() {
    return CompletableFuture.completedFuture(
        tournaments.values().stream().map(Tournament::getBrief).toList());
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getPlayers() {
    return CompletableFuture.completedFuture(
        players.values().stream().map(Player::getBrief).toList());
  }

  @Override
  public CompletableFuture<List<ClubBrief>> getClubs() {
    return CompletableFuture.completedFuture(clubs.values().stream().map(Club::getBrief).toList());
  }

  @Override
  public CompletableFuture<Tournament> getTournament(Tournament.Id id) {
    return CompletableFuture.completedFuture(tournaments.get(id));
  }

  @Override
  public CompletableFuture<Player> getPlayer(Player.Id id) {
    return CompletableFuture.completedFuture(players.get(id));
  }

  @Override
  public CompletableFuture<Club> getClub(Club.Id id) {
    return CompletableFuture.completedFuture(clubs.get(id));
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getClubMembers(Club.Id id) {
    return CompletableFuture.completedFuture(
        players.values().stream()
            .filter(p -> p.club().id().equals(id))
            .map(Player::getBrief)
            .toList());
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getTournamentArbiters(Tournament.Id id) {
    return CompletableFuture.completedFuture(
        arbiters.stream()
            .filter(v -> v.getKey().equals(id))
            .map(Pair::getValue)
            .map(players::get)
            .map(Player::getBrief)
            .toList());
  }
}
