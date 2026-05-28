package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record Title(@NotNull Title.Id id, @NotNull String name) {
  public interface Id {}
}
