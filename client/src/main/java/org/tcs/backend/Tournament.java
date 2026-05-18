package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public record Tournament(
    @NotNull Id id,
    @NotNull String name,
    @NotNull Timestamp start,
    @NotNull Timestamp end,
    @NotNull City.Id city) {
  public interface Id {}
}
