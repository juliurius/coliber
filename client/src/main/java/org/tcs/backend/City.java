package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public interface City {
  interface Id {}

  @NotNull Id getId();
  @NotNull String getName();
}
