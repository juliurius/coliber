package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Player(
    @NotNull Id id,
    @NotNull String name,
    @NotNull String surname,
    int ratingClassical,
    int ratingRapid,
    int ratingBlitz,
    ClubBrief club,
    @Nullable PlayerClass.Id playerClass,
    @Nullable ArbiterClass.Id arbiterClass,
    @Nullable Title.Id title) {
  public interface Id {}
  public record Data(@NotNull String name, @NotNull String surname) {}

  public PlayerBrief getBrief() {
    return new PlayerBrief(id, name, surname, ratingClassical);
  }
}
