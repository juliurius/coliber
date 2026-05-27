package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record Club(@NotNull Id id, @NotNull String name, City.Id city, PlayerBrief president) {
  public interface Id {}

  public ClubBrief getBrief() {
    return new ClubBrief(id, name, city);
  }
}
