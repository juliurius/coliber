package org.tcs.ui.system;

import javafx.scene.control.Label;
import org.tcs.backend.TournamentSystem;
import org.tcs.ui.util.SimpleListEntry;

public class ListEntry extends Label implements SimpleListEntry<TournamentSystem> {
  public final TournamentSystem id;

  public ListEntry(TournamentSystem system) {
    setText(system.name());
    this.id = system;
  }

  @Override
  public TournamentSystem id() {
    return id;
  }
}
