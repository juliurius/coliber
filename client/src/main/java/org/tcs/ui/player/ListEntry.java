package org.tcs.ui.player;

import javafx.scene.control.Label;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerBrief;

public class ListEntry extends Label {
  public final Player.Id id;

  public ListEntry(PlayerBrief brief) {
    setText(brief.name() + " " + brief.surname());
    this.id = brief.id();
  }
}
