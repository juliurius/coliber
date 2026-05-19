package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import org.tcs.Globals;
import org.tcs.backend.Backend;

import java.util.concurrent.CompletableFuture;

public class Tournaments extends BorderPane {
  public Tournaments(Backend backend, CompletableFuture<Globals> globals) {
    var list = new TournamentsList(backend);
    var details = new TournamentDetails();

    setCenter(list);

    list.setOnSelected(
      id ->
        backend
          .getTournament(id)
          .thenCombine(
            globals,
            (t, g) -> {
              Platform.runLater(() -> {
                details.setTournament(t, g);
                setCenter(details);
              });
              return null;
            }));
    details.setOnBack(() -> setCenter(list));
  }
}
