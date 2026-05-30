package org.tcs.backend;

public record PlayerFilter(boolean arbitersOnly) {
  public static final PlayerFilter ALL = new PlayerFilter(false);
}
