package org.tcs.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public sealed interface Nav permits Nav.Player, Nav.Tournament, Nav.Club, Nav.Tempo, Nav.TournamentSystem {
  sealed interface Player extends Nav permits Player.All, Player.Details, Player.Create {
    record All() implements Player {}
    record Details(@NotNull org.tcs.backend.Player.Id id) implements Player {}
    record Create() implements Player {}
  }
  record Tournament(@Nullable org.tcs.backend.Tournament.Id id) implements Nav {}
  record Club(@Nullable org.tcs.backend.Club.Id id) implements Nav {}
  sealed interface Tempo extends Nav permits Tempo.All, Tempo.Create {
    record All() implements Tempo {}
    record Create() implements Tempo {}
  }
  sealed interface TournamentSystem extends Nav permits TournamentSystem.All, TournamentSystem.Create {
    record All() implements TournamentSystem {}
    record Create() implements TournamentSystem {}
  }
}
