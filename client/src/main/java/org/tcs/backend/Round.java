package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record Round(@NotNull Id id) {
  public interface Id {}
}
