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
import org.tcs.backend.*;
import org.tcs.ui.Util;

public class TournamentInput extends VBox {
  private final SimpleObjectProperty<Tournament.Id> tournament = new SimpleObjectProperty<>();

  public TournamentInput(Backend backend) {
    this(backend, "Tournament");
  }

  public TournamentInput(Backend backend, String labelText) {
    var label = new Label(labelText + ": ");
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

    tournament.bind(list.getSelectionModel().selectedItemProperty().map(v -> v.id));

    var clear = new Button("Clear");
    clear.visibleProperty().bind(tournament.isNotNull());
    clear.setOnAction(_ -> list.getSelectionModel().clearSelection());

    backend
      .getTournaments()
      .thenAccept(
        tournaments ->
          Platform.runLater(() -> items.setAll(tournaments.stream().map(Entry::new).toList())));

    getChildren().addAll(Util.inline(label, search), list);
  }

  public Tournament.Id getValue() {
    return tournament.get();
  }

  private static class Entry extends Label {
    final Tournament.Id id;

    Entry(TournamentBrief p) {
      super(p.name());
      id = p.id();
    }
  }
}
