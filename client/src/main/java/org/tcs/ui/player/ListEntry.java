package org.tcs.ui.player;

import javafx.scene.control.Label;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerBrief;
import org.tcs.ui.util.SimpleListEntry;

public class ListEntry extends Label implements SimpleListEntry<Player.Id> {
  public final Player.Id id;

  public ListEntry(PlayerBrief brief) {
    setText(brief.name() + " " + brief.surname());
    this.id = brief.id();
  }

  @Override
  public Player.Id id() {
    return id;
  }
}
