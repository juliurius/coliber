package org.tcs.ui.util;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
  private final ListView<Entry> list = new ListView<>();
  private final ObservableList<Entry> items = FXCollections.observableArrayList();

  public PlayerInput(Backend backend) {
    this(backend, PlayerFilter.ALL, "Player");
  }

  public PlayerInput(Backend backend, String labelText) {
    this(backend, PlayerFilter.ALL, labelText);
  }

  public PlayerInput(Backend backend, PlayerFilter filter) {
    this(backend, filter, "Player");
  }

  public PlayerInput(Backend backend, PlayerFilter filter, String labelText) {
    this(() -> backend.getPlayers(filter), labelText);
  }

  public PlayerInput(Supplier<CompletableFuture<List<PlayerBrief>>> players, String labelText) {
    var label = new Label(labelText + ": ");
    var search = new TextField();
    var prompt = new SimpleStringProperty("");
    search.promptTextProperty().bind(prompt);
    label.setLabelFor(list);
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

    players
        .get()
        .thenAccept(
            playerList -> Platform.runLater(() -> setPlayers(playerList)));

    getChildren().addAll(Util.inline(label, search), list);
  }

  public Player.Id getValue() {
    return player.get();
  }

  public void setPlayers(List<PlayerBrief> players) {
    list.getSelectionModel().clearSelection();
    items.setAll(players.stream().map(Entry::new).toList());
  }

  private static class Entry extends Label {
    final Player.Id id;

    Entry(PlayerBrief p) {
      super(p.toString());
      id = p.id();
    }
  }
}
