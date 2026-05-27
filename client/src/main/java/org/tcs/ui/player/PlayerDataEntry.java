package org.tcs.ui.player;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Hyperlink;
import org.tcs.backend.PlayerBrief;
import org.tcs.ui.Nav;

import java.util.function.Consumer;

public class PlayerDataEntry extends Hyperlink {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>();

  public PlayerDataEntry(PlayerBrief brief) {
    setText(brief.name() + " " + brief.surname());
    setOnAction(_ -> onNav.get().accept(new Nav.Player(brief.id())));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }
}
