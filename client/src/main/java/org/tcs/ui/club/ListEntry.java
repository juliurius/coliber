package org.tcs.ui.club;

import javafx.scene.control.Label;
import org.tcs.backend.Club;
import org.tcs.backend.ClubBrief;

public class ListEntry extends Label {
  public final Club.Id id;

  public ListEntry(ClubBrief brief) {
    setText(brief.name());
    this.id = brief.id();
  }
}
