package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Tempo(@NotNull Id id, @Nullable String name, @Nullable String description) {
  public interface Id {}
}
