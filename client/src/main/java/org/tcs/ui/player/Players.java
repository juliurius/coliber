package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.ui.Nav;

import java.util.function.Consumer;

public class Players extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Player.Id> player =
    new SimpleObjectProperty<>(null);

  public Players(Backend backend) {
    var list = new PlayersList(backend);
    var details = new PlayerDetails();

    details.onNavProperty().bind(onNav);

    setCenter(list);
    centerProperty().bind(Bindings.when(player.isNull()).then((Node) list).otherwise(details));
    player.addListener(
      _ -> {
        if (player.get() != null) {
          backend
            .getPlayer(player.get())
            .thenAccept(t -> Platform.runLater(() -> details.playerProperty().set(t)));
        }
      });

    list.setOnSelected(id -> onNav.get().accept(new Nav.Player(id)));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Player.Id> playerProperty() {
    return player;
  }
}
