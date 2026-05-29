package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public record Tournament(
    @NotNull Id id,
    @NotNull String name,
    @NotNull Timestamp start,
    @NotNull Timestamp end,
    City.Id city,
    String address,
    @NotNull Tempo tempo,
    @NotNull TournamentSystem system,
    @NotNull PlayerBrief organiser,
    @NotNull PlayerBrief mainArbiter) {
  public interface Id {}

  public TournamentBrief getBrief() {
    return new TournamentBrief(id, name, start, end, city);
  }
}
