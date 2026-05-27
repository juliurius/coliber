package org.tcs.backend;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Backend {
  CompletableFuture<Map<City.Id, City>> getCities();
  CompletableFuture<Map<Tempo.Id, Tempo>> getTempos();
  CompletableFuture<Map<TournamentSystem.Id, TournamentSystem>> getTournamentSystems();
  CompletableFuture<List<TournamentBrief>> getTournaments();
  CompletableFuture<List<PlayerBrief>> getPlayers();
  CompletableFuture<List<ClubBrief>> getClubs();

  CompletableFuture<Tournament> getTournament(Tournament.Id id);
  CompletableFuture<Player> getPlayer(Player.Id id);
  CompletableFuture<Club> getClub(Club.Id id);

  CompletableFuture<List<PlayerBrief>> getClubMembers(Club.Id id);
  CompletableFuture<List<PlayerBrief>> getTournamentArbiters(Tournament.Id id);
}
