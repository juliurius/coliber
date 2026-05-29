package org.tcs.backend;

import org.jetbrains.annotations.Nullable;

public record Tempo(@Nullable String name, @Nullable String description) {
  public record Data(String name, String description) {}
}
