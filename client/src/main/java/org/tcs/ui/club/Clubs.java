package org.tcs.ui.club;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.ui.Nav;
import org.tcs.ui.util.SimpleList;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Clubs extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Nav.Club> nav =
    new SimpleObjectProperty<>(new Nav.Club.All());

  public Clubs(Backend backend, CompletableFuture<Globals> globals) {
    var list = new SimpleList<Club.Id>(f -> backend
      .getClubs()
      .thenAccept(
        clubs ->
          Platform.runLater(
            () -> f.accept(
              clubs.stream().map(ListEntry::new).toList()))));
    var details = new ClubDetails(backend);
    var creator = new Creator(backend, globals);

    creator.setOnBack(() -> onNav.get().accept(new Nav.Club.All()));

    globals.thenAccept(g -> details.globalsProperty().set(g));
    details.onNavProperty().bind(onNav);

    setCenter(list);
    centerProperty().bind(nav.map(nav -> {
      if (nav instanceof Nav.Club.All) {
        list.load();
        return list;
      } else if (nav instanceof Nav.Club.Details) {
        return details;
      }

      return creator;
    }));
    nav.addListener(
      _ -> {
        if (nav.get() instanceof Nav.Club.Details(Club.Id id)) {
          backend
            .getClub(id)
            .thenAccept(t -> Platform.runLater(() -> details.clubProperty().set(t)));
        }
      });

    list.setOnCreate(() -> onNav.get().accept(new Nav.Club.Create()));
    list.setOnSelected(id -> onNav.get().accept(new Nav.Club.Details(id)));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Nav.Club> navProperty() {
    return nav;
  }
}
