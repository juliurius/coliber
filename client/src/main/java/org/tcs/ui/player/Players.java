package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import org.tcs.backend.Backend;

public class Players extends BorderPane {
  public Players(Backend backend) {
    var list = new PlayersList(backend);
    var details = new PlayerDetails();

    setCenter(list);

    list.setOnSelected(
      id ->
        backend
          .getPlayer(id)
          .thenApply(
            t -> {
              Platform.runLater(() -> {
                details.setPlayer(t);
                setCenter(details);
              });
              return null;
            }));
    details.setOnBack(() -> setCenter(list));
  }
}
