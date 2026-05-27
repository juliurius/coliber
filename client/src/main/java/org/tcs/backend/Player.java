package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record Player(@NotNull Id id, @NotNull String name, @NotNull String surname, int rating, ClubBrief club) {
  public interface Id {}

  public PlayerBrief getBrief() {
    return new PlayerBrief(id, name, surname, rating);
  }
}
