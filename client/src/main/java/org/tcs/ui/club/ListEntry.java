package org.tcs.ui.club;

import javafx.scene.control.Label;
import org.tcs.backend.Club;
import org.tcs.backend.ClubBrief;
import org.tcs.ui.util.SimpleListEntry;

public class ListEntry extends Label implements SimpleListEntry<Club.Id> {
  public final Club.Id id;

  public ListEntry(ClubBrief brief) {
    setText(brief.name());
    this.id = brief.id();
  }

  @Override
  public Club.Id id() {
    return id;
  }
}
