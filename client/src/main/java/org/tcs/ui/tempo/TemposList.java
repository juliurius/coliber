package org.tcs.ui.tempo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;

public class TemposList extends VBox {
  public TemposList(Backend backend) {
    var list = new ListView<ListEntry>();

    var items = FXCollections.<ListEntry>observableArrayList();
    list.setItems(items);
    getChildren().add(list);

    backend
      .getTempos()
      .thenAccept(
        tempos ->
          Platform.runLater(
            () -> items.setAll(
              tempos.values().stream().map(ListEntry::new).toArray(ListEntry[]::new))));
  }
}
