package org.tcs.ui.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerBrief;
import org.tcs.backend.PlayerFilter;
import org.tcs.ui.Util;

public class PlayerInput extends VBox {
  private final SimpleObjectProperty<Player.Id> player = new SimpleObjectProperty<>();

  public PlayerInput(Backend backend) {
    this(backend, PlayerFilter.ALL);
  }

  public PlayerInput(Backend backend, PlayerFilter filter) {
    var label = new Label("Player");
    var search = new TextField();
    var prompt = new SimpleStringProperty("");
    search.promptTextProperty().bind(prompt);
    var list = new ListView<Entry>();
    label.setLabelFor(list);
    var items = FXCollections.<Entry>observableArrayList();
    var filtered = items.filtered(_ -> true);
    filtered
        .predicateProperty()
        .bind(
            search
                .textProperty()
                .map(s -> e -> e.getText().toLowerCase().contains(s.toLowerCase())));
    list.setItems(filtered);

    player.bind(list.getSelectionModel().selectedItemProperty().map(v -> v.id));

    var clear = new Button("Clear");
    clear.visibleProperty().bind(player.isNotNull());
    clear.setOnAction(_ -> list.getSelectionModel().clearSelection());

    backend
        .getPlayers(filter)
        .thenAccept(
            players ->
                Platform.runLater(() -> items.setAll(players.stream().map(Entry::new).toList())));

    getChildren().addAll(Util.inline(label, search), list);
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
