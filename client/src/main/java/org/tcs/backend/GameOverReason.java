package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record GameOverReason(@NotNull Id id, @NotNull String description, float winScore, float loseScore) {
  public interface Id {}
}
