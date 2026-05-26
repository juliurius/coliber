package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record PlayerBrief(@NotNull Player.Id id, @NotNull String name, @NotNull String surname, int rating) {
  @Override
  public @NotNull String toString() {
    return name + " " + surname + " (" + rating + ")";
  }
}