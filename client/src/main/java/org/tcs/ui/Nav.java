package org.tcs.ui;

import org.jetbrains.annotations.NotNull;

public sealed interface Nav permits Nav.Player, Nav.Tournament, Nav.Club, Nav.Tempo, Nav.TournamentSystem {
  sealed interface Player extends Nav permits Player.All, Player.Details, Player.Create {
    record All() implements Player {}
    record Details(@NotNull org.tcs.backend.Player.Id id) implements Player {}
    record Create() implements Player {}
  }
  sealed interface Tournament extends Nav permits Tournament.All, Tournament.Details, Tournament.Create {
    record All() implements Tournament {}
    record Details(@NotNull org.tcs.backend.Tournament.Id id) implements Tournament {}
    record Create() implements Tournament {}
  }
  sealed interface Club extends Nav permits Club.All, Club.Details, Club.Create {
    record All() implements Club {}
    record Details(@NotNull org.tcs.backend.Club.Id id) implements Club {}
    record Create() implements Club {}
  }
  sealed interface Tempo extends Nav permits Tempo.All, Tempo.Create {
    record All() implements Tempo {}
    record Create() implements Tempo {}
  }
  sealed interface TournamentSystem extends Nav permits TournamentSystem.All, TournamentSystem.Create {
    record All() implements TournamentSystem {}
    record Create() implements TournamentSystem {}
  }
}
