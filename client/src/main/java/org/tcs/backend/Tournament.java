package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public record Tournament(
    @NotNull Id id,
    @NotNull String name,
    @NotNull Timestamp start,
    @NotNull Timestamp end,
    @NotNull City.Id city,
    String address,
    @NotNull Tempo.Id tempo,
    @NotNull TournamentSystem.Id system,
    @NotNull Player.Id organiser,
    @NotNull Player.Id mainArbiter) {
  public interface Id {}

  public TournamentBrief getBrief() {
    return new TournamentBrief(id, name, start, end, city);
  }
}
