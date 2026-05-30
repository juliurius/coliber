package org.tcs.ui.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerBrief;

public class PlayerInput extends VBox {
  private final SimpleObjectProperty<Player.Id> player = new SimpleObjectProperty<>();

  public PlayerInput(Backend backend) {
    var label = new Label("Player");
    var list = new ListView<Entry>();
    label.setLabelFor(list);
    var items = FXCollections.<Entry>observableArrayList();
    list.setItems(items);

    player.bind(list.getSelectionModel().selectedItemProperty().map(v -> v.id));

    var clear = new Button("Clear");
    clear.visibleProperty().bind(player.isNotNull());
    clear.setOnAction(_ -> list.getSelectionModel().clearSelection());

    backend
      .getPlayers()
      .thenAccept(
        players ->
          Platform.runLater(() -> items.setAll(players.stream().map(Entry::new).toList())));

    getChildren().addAll(label, list);
  }

  public Player.Id getValue() {
    return player.get();
  }

  private static class Entry extends Label {
    final Player.Id id;

    Entry(PlayerBrief p) {
      super(p.toString());
      id = p.id();
    }
  }
}
