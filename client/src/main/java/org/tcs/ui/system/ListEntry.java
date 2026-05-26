package org.tcs.ui.system;

import javafx.scene.control.Label;
import org.tcs.backend.Tempo;
import org.tcs.backend.TournamentSystem;

public class ListEntry extends Label {
  public final TournamentSystem.Id id;

  public ListEntry(TournamentSystem system) {
    setText(system.name());
    this.id = system.id();
  }
}
