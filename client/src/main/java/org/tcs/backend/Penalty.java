package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;

public record Penalty(
    @NotNull Id id,
    @NotNull Date since,
    @NotNull Date until,
    @Nullable String reason,
    @NotNull Tournament.Id tournament,
    @NotNull PlayerBrief arbiter,
    @NotNull PenaltyRoleContext roleContext) {
  public interface Id {}

  public record Data(
      Date until,
      String reason,
      Tournament.Id tournament,
      Player.Id arbiter,
      PenaltyRoleContext.Id roleContext) {}
}
