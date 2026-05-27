package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Player(
    @NotNull Id id,
    @NotNull String name,
    @NotNull String surname,
    int rating,
    ClubBrief club,
    @Nullable PlayerClass.Id playerClass,
    @Nullable ArbiterClass.Id arbiterClass) {
  public interface Id {}

  public PlayerBrief getBrief() {
    return new PlayerBrief(id, name, surname, rating);
  }
}
