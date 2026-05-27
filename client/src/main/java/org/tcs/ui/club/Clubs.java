package org.tcs.ui.club;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.ui.Nav;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Clubs extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Club.Id> club =
    new SimpleObjectProperty<>(null);

  public Clubs(Backend backend, CompletableFuture<Globals> globals) {
    var list = new ClubsList(backend);
    var details = new ClubDetails();

    globals.thenAccept(g -> details.globalsProperty().set(g));
    details.onNavProperty().bind(onNav);

    setCenter(list);
    centerProperty().bind(Bindings.when(club.isNull()).then((Node) list).otherwise(details));
    club.addListener(
      _ -> {
        if (club.get() != null) {
          backend
            .getClub(club.get())
            .thenAccept(t -> Platform.runLater(() -> details.clubProperty().set(t)));
        }
      });

    list.setOnSelected(id -> onNav.get().accept(new Nav.Club(id)));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Club.Id> clubProperty() {
    return club;
  }
}
