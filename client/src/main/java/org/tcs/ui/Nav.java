package org.tcs.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface Nav permits Nav.Player, Nav.Tournament, Nav.Club {
  sealed interface Player extends Nav permits Player.All, Player.Details, Player.Create {
    record All() implements Player {}
    record Details(@NotNull org.tcs.backend.Player.Id id) implements Player {}
    record Create() implements Player {}
  }
  record Tournament(@Nullable org.tcs.backend.Tournament.Id id) implements Nav {}
  record Club(@Nullable org.tcs.backend.Club.Id id) implements Nav {}
}
