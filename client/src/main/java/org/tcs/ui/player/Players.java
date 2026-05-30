package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.ui.Nav;
import org.tcs.ui.util.SimpleList;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Players extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Nav.Player> nav =
    new SimpleObjectProperty<>(new Nav.Player.All());

  public Players(Backend backend, CompletableFuture<Globals> globals) {
    var list = new SimpleList<Player.Id>(f -> backend
      .getPlayers()
      .thenAccept(
        players ->
          Platform.runLater(
            () -> f.accept(
              players.stream().map(ListEntry::new).toList()))));
    var details = new PlayerDetails(backend);
    var creator = new Creator(backend);

    creator.setOnBack(() -> onNav.get().accept(new Nav.Player.All()));

    globals.thenAccept(g -> details.globalsProperty().set(g));
    details.onNavProperty().bind(onNav);

    setCenter(list);
    centerProperty().bind(nav.map(nav -> {
      if (nav instanceof Nav.Player.All) {
        list.load();
        return list;
      } else if (nav instanceof Nav.Player.Details) {
        return details;
      }

      return creator;
    }));
    nav.addListener(
      _ -> {
        if (nav.get() instanceof Nav.Player.Details(Player.Id id)) {
          backend
            .getPlayer(id)
            .thenAccept(t -> Platform.runLater(() -> details.playerProperty().set(t)));
        }
      });

    list.setOnCreate(() -> onNav.get().accept(new Nav.Player.Create()));
    list.setOnSelected(id -> onNav.get().accept(new Nav.Player.Details(id)));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Nav.Player> navProperty() {
    return nav;
  }
}
