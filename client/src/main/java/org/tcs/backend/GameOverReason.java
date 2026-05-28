package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record GameOverReason(@NotNull Id id, @NotNull String description) {
  public interface Id {}
}
