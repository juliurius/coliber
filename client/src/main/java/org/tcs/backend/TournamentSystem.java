package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record TournamentSystem(@NotNull Id id, @NotNull String name) {
  public interface Id {}
}
