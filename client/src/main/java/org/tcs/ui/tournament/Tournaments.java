package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Tournament;
import org.tcs.ui.Nav;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Tournaments extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Tournament.Id> tournament =
      new SimpleObjectProperty<>(null);

  public Tournaments(Backend backend, CompletableFuture<Globals> globals) {
    var list = new TournamentsList(backend);
    var details = new TournamentDetails(backend);

    globals.thenAccept(g -> details.globalsProperty().set(g));
    details.onNavProperty().bind(onNav);

    setCenter(list);
    centerProperty().bind(Bindings.when(tournament.isNull()).then((Node) list).otherwise(details));
    tournament.addListener(
        _ -> {
          if (tournament.get() != null) {
            backend
                .getTournament(tournament.get())
                .thenAccept(t -> Platform.runLater(() -> details.tournamentProperty().set(t)));
          }
        });

    list.setOnSelected(id -> onNav.get().accept(new Nav.Tournament(id)));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Tournament.Id> tournamentProperty() {
    return tournament;
  }
}
