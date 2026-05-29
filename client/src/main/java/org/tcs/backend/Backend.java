package org.tcs.backend;

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
  CompletableFuture<List<PlayerBrief>> getPlayers();
  CompletableFuture<List<ClubBrief>> getClubs();
  CompletableFuture<List<Tempo>> getTempos();
  CompletableFuture<List<TournamentSystem>> getTournamentSystems();

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
}
