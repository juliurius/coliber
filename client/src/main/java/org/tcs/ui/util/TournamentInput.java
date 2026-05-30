package org.tcs.ui.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.backend.*;

public class TournamentInput extends VBox {
  private final SimpleObjectProperty<Tournament.Id> tournament = new SimpleObjectProperty<>();

  public TournamentInput(Backend backend) {
    var label = new Label("Tournament:");
    var list = new ListView<Entry>();
    label.setLabelFor(list);
    var items = FXCollections.<Entry>observableArrayList();
    list.setItems(items);

    tournament.bind(list.getSelectionModel().selectedItemProperty().map(v -> v.id));

    var clear = new Button("Clear");
    clear.visibleProperty().bind(tournament.isNotNull());
    clear.setOnAction(_ -> list.getSelectionModel().clearSelection());

    backend
      .getTournaments()
      .thenAccept(
        tournaments ->
          Platform.runLater(() -> items.setAll(tournaments.stream().map(Entry::new).toList())));

    getChildren().addAll(label, list);
  }

  public Tournament.Id getValue() {
    return tournament.get();
  }

  private static class Entry extends Label {
    final Tournament.Id id;

    Entry(TournamentBrief p) {
      super(p.toString());
      id = p.id();
    }
  }
}
