package org.tcs.backend;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface Backend {
  CompletableFuture<Map<City.Id, City>> getCities();
  CompletableFuture<List<Tournament>> getTournaments();
  CompletableFuture<List<Player>> getPlayers();
}
