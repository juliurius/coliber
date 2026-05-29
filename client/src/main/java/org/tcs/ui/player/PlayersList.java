package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;

import java.util.function.Consumer;

public class PlayersList extends VBox {
  private Consumer<Player.Id> onSelected;
  private Runnable onCreate = () -> {};
  private final ObservableList<ListEntry> items = FXCollections.observableArrayList();
  private final Backend backend;

  public PlayersList(Backend backend) {
    this.backend = backend;
    var create = new Button("Create");
    create.setOnAction(_ -> onCreate.run());
    getChildren().add(create);

    var list = new ListView<ListEntry>();

    list.setItems(items);
    getChildren().add(list);

    list.setOnMouseClicked(
      _ -> {
        if (onSelected == null) return;

        ListEntry item = list.getSelectionModel().getSelectedItem();
        list.getSelectionModel().clearSelection();
        if (item == null) return;

        onSelected.accept(item.id);
      });
  }

  public void load() {
    backend
      .getPlayers()
      .thenAccept(
        players ->
          Platform.runLater(
            () -> items.setAll(
              players.stream().map(ListEntry::new).toArray(ListEntry[]::new))));
  }

  public void setOnSelected(Consumer<Player.Id> onSelected) {
    this.onSelected = onSelected;
  }

  public void setOnCreate(Runnable onCreate) {
    this.onCreate = onCreate;
  }
}
