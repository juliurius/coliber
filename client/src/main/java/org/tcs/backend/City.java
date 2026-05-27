package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record City(@NotNull Id id, @NotNull String name) {
  public interface Id {}
}
