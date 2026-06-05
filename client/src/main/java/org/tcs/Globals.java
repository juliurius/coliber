package org.tcs;

import org.jetbrains.annotations.NotNull;
import org.tcs.backend.*;

import java.util.Map;

public record Globals(
  @NotNull Map<City.Id, City> cities,
  @NotNull Map<PlayerClass.Id, PlayerClass> playerClasses,
  @NotNull Map<ArbiterClass.Id, ArbiterClass> arbiterClasses,
  @NotNull Map<Title.Id, Title> titles,
  @NotNull Map<GameOverReason.Id, GameOverReason> gameOverReasons,
  @NotNull Map<PenaltyRoleContext.Id, PenaltyRoleContext> penaltyRoleContexts
) {
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

  public GameOverReason gameOverReason(GameOverReason.Id id) {
    return gameOverReasons.get(id);
  }

  public PenaltyRoleContext penaltyRoleContext(PenaltyRoleContext.Id id) {
    return penaltyRoleContexts.get(id);
  }
}
