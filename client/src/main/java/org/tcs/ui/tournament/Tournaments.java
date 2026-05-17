package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;
import org.tcs.backend.Tournament;

public class Tournaments extends VBox {
  public Tournaments(Backend backend) {
    var list = new ListView<>();
    var items = FXCollections.observableArrayList();
    list.setItems(items);
    getChildren().add(list);

    backend
        .getTournaments()
        .thenAccept(
            tournaments -> {
              Platform.runLater(() -> items.setAll(tournaments.stream().map(Tournament::getName).toArray()));
            });
  }
}
