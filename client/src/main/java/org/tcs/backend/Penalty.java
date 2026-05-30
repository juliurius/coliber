package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;

public record Penalty(
    @NotNull Date until,
    @Nullable String reason,
    @NotNull Tournament.Id tournament,
    @NotNull PlayerBrief arbiter) {
  public record Data(Date until, String reason, Tournament.Id tournament, Player.Id arbiter) {}
}
