package org.tcs.backend.mock;

import javafx.util.Pair;
import org.tcs.backend.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Mock implements Backend {
  private static final Timestamp epoch = Timestamp.from(Instant.EPOCH);

  record FakeId(int id)
      implements City.Id,
          Tournament.Id,
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
      new ArrayList<>(List.of(new Tempo("Blitz", "3|2"), new Tempo("Bullet", "1|0")));
  final List<TournamentSystem> systems = new ArrayList<>(List.of(new TournamentSystem("Swiss")));
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
  final Map<FakeId, Player> players =
      new HashMap<>(
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
                  new FakeId(1))));
  final Map<Club.Id, Club> clubs =
      new HashMap<>(
          Map.of(
              new FakeId(0),
              new Club(
                  new FakeId(0),
                  "Szachiści z Opola",
                  new FakeId(1),
                  players.get(new FakeId(0)).getBrief()),
              new FakeId(1),
              new Club(new FakeId(1), "Wisła Kraków", new FakeId(0), null)));
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
              tempos.get(1),
              systems.getFirst(),
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
              tempos.get(1),
              systems.getFirst(),
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
                      true, 1, 0, new FakeId(0), players.get(new FakeId(0)).getBrief()))));

  @Override
  public CompletableFuture<Map<City.Id, City>> getCities() {
    return CompletableFuture.completedFuture(
        cities.stream().collect(Collectors.toMap(City::id, v -> v)));
  }

  @Override
  public CompletableFuture<List<Tempo>> getTempos() {
    return CompletableFuture.completedFuture(new ArrayList<>(tempos));
  }

  @Override
  public CompletableFuture<List<TournamentSystem>> getTournamentSystems() {
    return CompletableFuture.completedFuture(new ArrayList<>(systems));
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
    return CompletableFuture.completedFuture(players.get((FakeId) id));
  }

  @Override
  public CompletableFuture<Club> getClub(Club.Id id) {
    return CompletableFuture.completedFuture(clubs.get(id));
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getClubMembers(Club.Id id) {
    return CompletableFuture.completedFuture(
        players.values().stream()
            .filter(p -> p.club() != null && p.club().id().equals(id))
            .map(Player::getBrief)
            .toList());
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getTournamentArbiters(Tournament.Id id) {
    return CompletableFuture.completedFuture(
        arbiters.stream()
            .filter(v -> v.getKey().equals(id))
            .map(Pair::getValue)
            .map(key -> players.get((FakeId) key))
            .map(Player::getBrief)
            .toList());
  }

  @Override
  public CompletableFuture<List<PlayerBrief>> getTournamentPlayers(Tournament.Id id) {
    return CompletableFuture.completedFuture(
        tournamentPlayers.stream()
            .filter(v -> v.getKey().equals(id))
            .map(Pair::getValue)
            .map(key -> players.get((FakeId) key))
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

  @Override
  public CompletableFuture<String> createPlayer(Player.Data data) {
    int id =
        players.keySet().stream()
            .max(Comparator.comparingInt(v -> v.id))
            .map(fakeId -> fakeId.id() + 1)
            .orElse(0);
    players.put(
        new FakeId(id),
        new Player(new FakeId(id), data.name(), data.surname(), 1000, null, null, null, null));
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<String> createTempo(Tempo.Data data) {
    tempos.add(new Tempo(data.name(), data.description()));
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<String> createTournamentSystem(TournamentSystem.Data data) {
    systems.add(new TournamentSystem(data.name()));
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<String> createClub(Club.Data data) {
    clubs.put(
        new FakeId(clubs.size()), new Club(new FakeId(clubs.size()), data.name(), null, null));
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<String> addClubMember(Club.Id club, Player.Id player) {
    var old = players.get((FakeId) player);
    var n =
        new Player(
            player,
            old.name(),
            old.surname(),
            old.rating(),
            clubs.get(club).getBrief(),
            old.playerClass(),
            old.arbiterClass(),
            old.title());
    players.put((FakeId) player, n);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<String> setClubPresident(Club.Id club, Player.Id player) {
    var old = clubs.get(club);
    var n = new Club(club, old.name(), old.city(), player == null ? null : players.get((FakeId) player).getBrief());
    clubs.put(club, n);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public void removeClubMember(Player.Id player) {
    var old = players.get((FakeId) player);
    var n =
      new Player(
        player,
        old.name(),
        old.surname(),
        old.rating(),
        null,
        old.playerClass(),
        old.arbiterClass(),
        old.title());
    players.put((FakeId) player, n);
  }

  @Override
  public CompletableFuture<String> setPlayerArbiterClass(Player.Id player, ArbiterClass.Id arbiterClass) {
    var old = players.get((FakeId) player);
    var n =
      new Player(
        player,
        old.name(),
        old.surname(),
        old.rating(),
        old.club(),
        old.playerClass(),
        arbiterClass,
        old.title());
    players.put((FakeId) player, n);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<String> setPlayerPlayerClass(Player.Id player, PlayerClass.Id playerClass, Tournament.Id tournament) {
    var old = players.get((FakeId) player);
    var n =
      new Player(
        player,
        old.name(),
        old.surname(),
        old.rating(),
        old.club(),
        playerClass,
        old.arbiterClass(),
        old.title());
    players.put((FakeId) player, n);
    return CompletableFuture.completedFuture(null);
  }

  @Override
  public CompletableFuture<String> setPlayerTitle(Player.Id player, Title.Id title, Tournament.Id tournament) {
    var old = players.get((FakeId) player);
    var n =
      new Player(
        player,
        old.name(),
        old.surname(),
        old.rating(),
        old.club(),
        old.playerClass(),
        old.arbiterClass(),
        title);
    players.put((FakeId) player, n);
    return CompletableFuture.completedFuture(null);
  }
}
