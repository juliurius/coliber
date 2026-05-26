package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;

import java.util.function.Consumer;

public class PlayersList extends VBox {
  private Consumer<Player.Id> onSelected;

  public PlayersList(Backend backend) {
    var list = new ListView<ListEntry>();

    var items = FXCollections.<ListEntry>observableArrayList();
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

    backend
      .getPlayers()
      .thenAccept(
        players ->
          Platform.runLater(
            () -> {
              System.out.println("Players: " + players);
              items.setAll(
                players.stream().map(ListEntry::new).toArray(ListEntry[]::new));
            }));
  }

  public void setOnSelected(Consumer<Player.Id> onSelected) {
    this.onSelected = onSelected;
  }}
