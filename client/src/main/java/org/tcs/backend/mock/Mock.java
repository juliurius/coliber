package org.tcs.backend.mock;

import org.tcs.backend.Backend;
import org.tcs.backend.City;
import org.tcs.backend.Player;
import org.tcs.backend.Tournament;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mock implements Backend {
  record FakeId(int id) implements City.Id, Tournament.Id {}

  @Override
  public CompletableFuture<Map<City.Id, City>> getCities() {
    return CompletableFuture.completedFuture(
        Stream.of(new City(new FakeId(0), "Opole"), new City(new FakeId(1), "Kraków"))
            .collect(Collectors.toMap(City::id, v -> v)));
  }

  @Override
  public CompletableFuture<List<Tournament>> getTournaments() {
    var epoch = Timestamp.from(Instant.EPOCH);
    return CompletableFuture.supplyAsync(
        () ->
            List.of(
                new Tournament(new FakeId(0), "Test 1", epoch, epoch, new FakeId(0)),
                new Tournament(new FakeId(1), "Test 2", epoch, epoch, new FakeId(1))),
        CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS));
  }

  @Override
  public CompletableFuture<List<Player>> getPlayers() {
    return CompletableFuture.supplyAsync(
        List::of, CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS));
  }
}
