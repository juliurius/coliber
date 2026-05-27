package org.tcs.ui.tournament;

import javafx.scene.control.Label;
import org.tcs.backend.Tournament;
import org.tcs.backend.TournamentBrief;

public class ListEntry extends Label {
  public final Tournament.Id id;

  public ListEntry(TournamentBrief brief) {
    setText(brief.name());
    this.id = brief.id();
  }
}
