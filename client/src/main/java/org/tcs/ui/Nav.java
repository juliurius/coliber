package org.tcs.ui;

import org.jetbrains.annotations.Nullable;

public sealed interface Nav permits Nav.Player, Nav.Tournament, Nav.Club {
  record Player(@Nullable org.tcs.backend.Player.Id id) implements Nav {}
  record Tournament(@Nullable org.tcs.backend.Tournament.Id id) implements Nav {}
  record Club(@Nullable org.tcs.backend.Club.Id id) implements Nav {}
}
