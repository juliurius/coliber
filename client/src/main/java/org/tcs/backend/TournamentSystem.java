package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record TournamentSystem(@NotNull String name) {
  public record Data(String name) {}
}
