package org.tcs;

import org.jetbrains.annotations.NotNull;
import org.tcs.backend.*;

import java.util.Map;

public record Globals(
  @NotNull Map<City.Id, City> cities,
  @NotNull Map<Tempo.Id, Tempo> tempos,
  @NotNull Map<TournamentSystem.Id, TournamentSystem> tournamentSystems,
  @NotNull Map<PlayerClass.Id, PlayerClass> playerClasses,
  @NotNull Map<ArbiterClass.Id, ArbiterClass> arbiterClasses,
  @NotNull Map<Title.Id, Title> titles
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

  public PlayerClass playerClass(PlayerClass.Id id) {
    return playerClasses.get(id);
  }

  public ArbiterClass arbiterClass(ArbiterClass.Id id) {
    return arbiterClasses.get(id);
  }

  public Title title(Title.Id id) {
    return titles.get(id);
  }
}
