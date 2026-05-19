package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public record TournamentBrief(
  @NotNull Tournament.Id id,
  @NotNull String name,
  @NotNull Timestamp start,
  @NotNull Timestamp end,
  @NotNull City.Id city) {}
