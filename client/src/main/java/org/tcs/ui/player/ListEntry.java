package org.tcs.ui.player;

import javafx.scene.control.Label;
import org.tcs.backend.Player;
import org.tcs.backend.RankingEntry;
import org.tcs.ui.util.SimpleListEntry;

public class ListEntry extends Label implements SimpleListEntry<Player.Id> {
  public final Player.Id id;

  public ListEntry(RankingEntry entry) {
    var player = entry.player();
    setText(entry.position() + ". " + player.name() + " " + player.surname() + " (" + player.rating() + ")");
    this.id = player.id();
  }

  @Override
  public Player.Id id() {
    return id;
  }
}
