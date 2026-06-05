package org.tcs.backend;

public record PlayerFilter(boolean arbitersOnly) {
  public static final PlayerFilter ALL = new PlayerFilter(false);
  public static final PlayerFilter ARBITERS_ONLY = new PlayerFilter(true);
}
