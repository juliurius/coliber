package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Tournament;
import org.tcs.ui.Nav;
import org.tcs.ui.util.SimpleList;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Tournaments extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Nav.Tournament> nav =
      new SimpleObjectProperty<>(new Nav.Tournament.All());

  public Tournaments(Backend backend, CompletableFuture<Globals> globals) {
    var list = new SimpleList<Tournament.Id>(f -> backend
        .getTournaments()
        .thenAccept(
            tournaments ->
                Platform.runLater(
                    () -> f.accept(
                        tournaments.stream().map(ListEntry::new).toList()))));
    var details = new TournamentDetails(backend);
    var creator = new Creator(backend, globals);

    creator.setOnBack(() -> onNav.get().accept(new Nav.Tournament.All()));

    globals.thenAccept(g -> details.globalsProperty().set(g));
    details.onNavProperty().bind(onNav);

    setCenter(list);
    centerProperty().bind(nav.map(nav -> {
      if (nav instanceof Nav.Tournament.All) {
        list.load();
        return list;
      } else if (nav instanceof Nav.Tournament.Details) {
        return details;
      }

      return creator;
    }));
    nav.addListener(
        _ -> {
          if (nav.get() instanceof Nav.Tournament.Details(Tournament.Id id)) {
            backend
                .getTournament(id)
                .thenAccept(t -> Platform.runLater(() -> details.tournamentProperty().set(t)));
          }
        });

    list.setOnCreate(() -> onNav.get().accept(new Nav.Tournament.Create()));
    list.setOnSelected(id -> onNav.get().accept(new Nav.Tournament.Details(id)));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Nav.Tournament> navProperty() {
    return nav;
  }
}
