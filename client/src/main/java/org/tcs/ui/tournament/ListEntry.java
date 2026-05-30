package org.tcs.ui.tournament;

import javafx.scene.control.Label;
import org.tcs.backend.Tournament;
import org.tcs.backend.TournamentBrief;
import org.tcs.ui.util.SimpleListEntry;

public class ListEntry extends Label implements SimpleListEntry<Tournament.Id> {
  public final Tournament.Id id;

  public ListEntry(TournamentBrief brief) {
    setText(brief.name());
    this.id = brief.id();
  }

  @Override
  public Tournament.Id id() {
    return id;
  }
}
