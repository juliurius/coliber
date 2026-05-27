package org.tcs;

import org.jetbrains.annotations.NotNull;
import org.tcs.backend.City;
import org.tcs.backend.Tempo;
import org.tcs.backend.TournamentSystem;

import java.util.Map;

public record Globals(
  @NotNull Map<City.Id, City> cities,
  @NotNull Map<Tempo.Id, Tempo> tempos,
  @NotNull Map<TournamentSystem.Id, TournamentSystem> tournamentSystems
) {
  public Tempo tempo(Tempo.Id id) {
    return tempos.get(id);
  }

  public TournamentSystem tournamentSystem(TournamentSystem.Id id) {
    return tournamentSystems.get(id);
  }

  public City city(City.Id id) {
    return cities.get(id);
  }
}
