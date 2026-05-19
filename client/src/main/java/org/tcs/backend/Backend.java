package org.tcs.backend;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Backend {
  CompletableFuture<Map<City.Id, City>> getCities();
  CompletableFuture<Map<Tempo.Id, Tempo>> getTempos();
  CompletableFuture<Map<TournamentSystem.Id, TournamentSystem>> getTournamentSystems();
  CompletableFuture<List<TournamentBrief>> getTournaments();
  CompletableFuture<List<Player>> getPlayers();

  CompletableFuture<Tournament> getTournament(Tournament.Id id);
}
