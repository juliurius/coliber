package org.tcs.backend;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Backend {
  CompletableFuture<Map<City.Id, City>> getCities();
  CompletableFuture<Map<PlayerClass.Id, PlayerClass>> getPlayerClasses();
  CompletableFuture<Map<ArbiterClass.Id, ArbiterClass>> getArbiterClasses();
  CompletableFuture<Map<Title.Id, Title>> getTitles();
  CompletableFuture<Map<GameOverReason.Id, GameOverReason>> getGameOverReasons();

  CompletableFuture<List<TournamentBrief>> getTournaments();

  default CompletableFuture<List<PlayerBrief>> getPlayers() {
    return getPlayers(PlayerFilter.ALL);
  }

  CompletableFuture<List<PlayerBrief>> getPlayers(PlayerFilter filter);
  CompletableFuture<List<RankingEntry>> getRanking();
  CompletableFuture<List<PlayerBrief>> getArbiters();
  CompletableFuture<List<ClubBrief>> getClubs();
  CompletableFuture<List<Tempo>> getTempos();
  CompletableFuture<List<TournamentSystem>> getTournamentSystems();

  CompletableFuture<Tournament.Id> createTournament(
      String name,
      Timestamp start,
      Timestamp end,
      City.Id city,
      String address,
      Tempo tempo,
      TournamentSystem system,
      Player.Id organiser,
      Player.Id mainArbiter);

  CompletableFuture<Tournament> getTournament(Tournament.Id id);
  CompletableFuture<Player> getPlayer(Player.Id id);
  CompletableFuture<Club> getClub(Club.Id id);

  CompletableFuture<List<PlayerBrief>> getClubMembers(Club.Id id);
  CompletableFuture<List<PlayerBrief>> getTournamentArbiters(Tournament.Id id);
  CompletableFuture<List<PlayerBrief>> getTournamentPlayers(Tournament.Id id);
  CompletableFuture<List<Round>> getTournamentRounds(Tournament.Id id);
  CompletableFuture<List<Game>> getRoundGames(Round.Id id);
  CompletableFuture<List<Penalty>> getPlayerPenalties(Player.Id id);
  CompletableFuture<List<Norm>> getPlayerNorms(Player.Id id);

  /** Returns error message if something went wrong, `null` otherwise */
  CompletableFuture<String> createPlayer(Player.Data data);
  CompletableFuture<String> createTempo(Tempo.Data data);
  CompletableFuture<String> createTournamentSystem(TournamentSystem.Data data);
  CompletableFuture<String> createClub(Club.Data data);

  CompletableFuture<String> addTournamentPlayer(Tournament.Id tournament, Player.Id player);
  CompletableFuture<String> addTournamentArbiter(Tournament.Id tournament, Player.Id player);
  CompletableFuture<String> generateSwissRound(Tournament.Id tournament, Timestamp start, Timestamp end);
//  CompletableFuture<String> createGame(Round.Id round, Player.Id white, Player.Id black);
//  CompletableFuture<String> setGameResult(
//          Round.Id round, Player.Id white, boolean whiteWon,
//          GameOverReason.Id reason, Player.Id arbiter,
//          int whiteRatingChange, int blackRatingChange);
//  CompletableFuture<String> closeTournament(Tournament.Id tournament);

  CompletableFuture<String> addClubMember(Club.Id clubId, Player.Id playerId);
  CompletableFuture<String> setClubPresident(Club.Id clubId, Player.Id playerId);
  void removeClubMember(Player.Id playerId);
  CompletableFuture<String> setPlayerArbiterClass(Player.Id playerId, ArbiterClass.Id arbiterClass);
  CompletableFuture<String> setPlayerPlayerClass(Player.Id playerId, PlayerClass.Id playerClass, Tournament.Id tournament);
  CompletableFuture<String> setPlayerTitle(Player.Id playerId, Title.Id title, Tournament.Id tournament);
  CompletableFuture<String> addPlayerPenalty(Player.Id playerId, Penalty.Data data);
}
