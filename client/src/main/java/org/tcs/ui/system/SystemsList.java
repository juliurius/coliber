package org.tcs.ui.system;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;

public class SystemsList extends VBox {
  public SystemsList(Backend backend) {
    var list = new ListView<ListEntry>();

    var items = FXCollections.<ListEntry>observableArrayList();
    list.setItems(items);
    getChildren().add(list);

    backend
      .getTournamentSystems()
      .thenAccept(
        systems ->
          Platform.runLater(
            () -> items.setAll(
              systems.values().stream().map(ListEntry::new).toArray(ListEntry[]::new))));
  }
}
