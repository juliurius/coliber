package org.tcs.backend.mock;

import org.jetbrains.annotations.NotNull;
import org.tcs.backend.Backend;
import org.tcs.backend.City;
import org.tcs.backend.Player;
import org.tcs.backend.Tournament;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Mock implements Backend {
  record CityId(int id) implements City.Id {}
  record MockCity(int id, String name) implements City {
    @Override
    public @NotNull Id getId() {
      return new CityId(id);
    }

    @Override
    public @NotNull String getName() {
      return name;
    }
  }

  @Override
  public CompletableFuture<Map<City.Id, City>> getCities() {
    return CompletableFuture.completedFuture(
        Stream.of(new MockCity(0, "Opole"), new MockCity(1, "Kraków"))
            .collect(Collectors.toMap(MockCity::getId, v -> v)));
  }

  @Override
  public CompletableFuture<List<Tournament>> getTournaments() {
    return CompletableFuture.supplyAsync(() -> List.of(
      new Tournament() {
        @Override
        public Id getId() {
          return new Tournament.Id() {};
        }

        @Override
        public String getName() {
          return "Test Tournament";
        }

        @Override
        public Timestamp getStart() {
          return null;
        }

        @Override
        public Timestamp getEnd() {
          return null;
        }

        @Override
        public City.Id getCity() {
          return null;
        }
      }
    ), CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS));
  }

  @Override
  public CompletableFuture<List<Player>> getPlayers() {
    return CompletableFuture.supplyAsync(List::of, CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS));
  }
}
