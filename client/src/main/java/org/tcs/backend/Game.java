package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Game(@NotNull PlayerBrief white, @NotNull PlayerBrief black, @Nullable Over over) {
  public record Over(
      boolean whiteWon,
      int whiteRatingChange,
      int blackRatingChange,
      @NotNull GameOverReason.Id reason,
      @NotNull PlayerBrief arbiter) {}
}
