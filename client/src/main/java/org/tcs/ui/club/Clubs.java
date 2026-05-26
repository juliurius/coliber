package org.tcs.ui.club;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import org.tcs.Globals;
import org.tcs.backend.Backend;

import java.util.concurrent.CompletableFuture;

public class Clubs extends BorderPane {
  public Clubs(Backend backend, CompletableFuture<Globals> globals) {
    var list = new ClubsList(backend);
    var details = new ClubDetails();

    setCenter(list);

    list.setOnSelected(
      id ->
        backend
          .getClub(id)
          .thenCombine(
            globals,
            (t, g) -> {
              Platform.runLater(() -> {
                details.setClub(t, g);
                setCenter(details);
              });
              return null;
            }));
    details.setOnBack(() -> setCenter(list));
  }
}
