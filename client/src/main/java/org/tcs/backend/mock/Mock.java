package org.tcs.backend.mock;

import javafx.util.Pair;
import org.tcs.backend.*;

import java.sql.Date;
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
      implements City.Id,
          Tournament.Id,
          Tempo.Id,
          TournamentSystem.Id,
          Player.Id,
          Club.Id,
          PlayerClass.Id,
          ArbiterClass.Id,
          Title.Id,
          GameOverReason.Id,
          Round.Id {}

  final List<City> cities =
      List.of(new City(new FakeId(0), "Kraków"), new City(new FakeId(1), "Opole"));
  final List<Tempo> tempos =
      List.of(new Tempo(new FakeId(0), "Blitz", "3|2"), new Tempo(new FakeId(1), "Bullet", "1|0"));
  final List<TournamentSystem> systems = List.of(new TournamentSystem(new FakeId(0), "Swiss"));
  final List<PlayerClass> playerClasses =
      List.of(new PlayerClass(new FakeId(0), "Good"), new PlayerClass(new FakeId(1), "Bad"));
  final List<ArbiterClass> arbiterClasses =
      List.of(new ArbiterClass(new FakeId(0), "Pretty"), new ArbiterClass(new FakeId(1), "Ugly"));
  final List<Title> titles =
      List.of(new Title(new FakeId(0), "Master"), new Title(new FakeId(1), "Grandmaster"));
  final List<GameOverReason> gameOverReasons =
      List.of(
          new GameOverReason(new FakeId(0), "won", 1.0f, 0.0f),
          new GameOverReason(new FakeId(1), "draw", 0.0f, 0.0f));
  final Map<Player.Id, Player> players =
      Map.of(
          new FakeId(0),
          new Player(
              new FakeId(0),
              "Magnus",
              "Carlsen",
              2882,
              new ClubBrief(new FakeId(0), "Szachiści z Opola", new FakeId(1)),
              new FakeId(0),
              new FakeId(0),
              new FakeId(0)),
          new FakeId(1),
          new Player(
              new FakeId(1),
              "Carlos",
              "Magnussen",
              9001,
              new ClubBrief(new FakeId(1), "Wisła Kraków", new FakeId(0)),
              null,
              new FakeId(1),
              new FakeId(1)));
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
  final Map<Player.Id, List<Penalty>> penalties =
      Map.of(
          new FakeId(0),
          List.of(
              new Penalty(
                  new Date(1000000),
                  "cheated",
                  new FakeId(0),
                  players.get(new FakeId(1)).getBrief())),
          new FakeId(1),
          List.of());
  final Map<Player.Id, List<Norm>> norms =
      Map.of(
          new FakeId(0),
          List.of(),
          new FakeId(1),
          List.of(new Norm(tournaments.get(new FakeId(0)).getBrief(), new FakeId(1))));
  final Set<Pair<Tournament.Id, Player.Id>> arbiters =
      Set.of(new Pair<>(new FakeId(0), new FakeId(0)), new Pair<>(new FakeId(1), new FakeId(1)));
  final Set<Pair<Tournament.Id, Player.Id>> tournamentPlayers =
      Set.of(
          new Pair<>(new FakeId(0), new FakeId(0)),
          new Pair<>(new FakeId(1), new FakeId(1)),
          new Pair<>(new FakeId(0), new FakeId(1)),
          new Pair<>(new FakeId(1), new FakeId(0)));
  final Map<Tournament.Id, List<Round>> rounds =
      Map.of(new FakeId(0), List.of(new Round(new FakeId(0))), new FakeId(1), List.of());
  final Map<Round.Id, List<Game>> games =
      Map.of(
          new FakeId(0),
          List.of(
              new Game(
                  players.get(new FakeId(0)).getBrief(),
                  players.get(new FakeId(1)).getBrief(),
                  new Game.Over(
                      true, gameOverReasons.getFirst(), players.get(new FakeId(0)).getBrief()))));

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
  public CompletableFuture<Map<Title.Id, Title>> getTitles() {
    return CompletableFuture.completedFuture(
        titles.stream().collect(Collectors.toMap(Title::id, v -> v)));
  }

  @Override
  public CompletableFuture<Map<GameOverReason.Id, GameOverReason>> getGameOverReasons() {
    return CompletableFuture.completedFuture(
        gameOverReasons.stream().collect(Collectors.toMap(GameOverReason::id, v -> v)));
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

  @Override
  public CompletableFuture<List<PlayerBrief>> getTournamentPlayers(Tournament.Id id) {
    return CompletableFuture.completedFuture(
        tournamentPlayers.stream()
            .filter(v -> v.getKey().equals(id))
            .map(Pair::getValue)
            .map(players::get)
            .map(Player::getBrief)
            .toList());
  }

  @Override
  public CompletableFuture<List<Round>> getTournamentRounds(Tournament.Id id) {
    return CompletableFuture.completedFuture(rounds.get(id));
  }

  @Override
  public CompletableFuture<List<Game>> getRoundGames(Round.Id id) {
    return CompletableFuture.completedFuture(games.get(id));
  }

  @Override
  public CompletableFuture<List<Penalty>> getPlayerPenalties(Player.Id id) {
    return CompletableFuture.completedFuture(penalties.get(id));
  }

  @Override
  public CompletableFuture<List<Norm>> getPlayerNorms(Player.Id id) {
    return CompletableFuture.completedFuture(norms.get(id));
  }
}
