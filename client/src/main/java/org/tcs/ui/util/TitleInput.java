package org.tcs.ui.util;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.tcs.Globals;
import org.tcs.backend.Title;

public class TitleInput extends HBox {
  private final SimpleObjectProperty<Title> titles = new SimpleObjectProperty<>();

  public TitleInput(CompletableFuture<Globals> globals) {
    var items = FXCollections.<Title>observableArrayList();
    var label = new Label("Title: ");
    var choice = new ComboBox<Title>();
    choice.setItems(items);
    choice.valueProperty().bindBidirectional(titles);
    choice.setPromptText("-");
    choice.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(Title item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText("-");
        } else {
          setText(item.name());
        }
      }
    });
    choice.setCellFactory(_ -> new ListCell<>() {
      @Override
      protected void updateItem(Title item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
          setText("-");
        } else {
          setText(item.name());
        }
      }
    });
    label.setLabelFor(choice);

    var clear = new Button("Clear");
    clear.setOnAction(_ -> titles.set(null));
    clear.visibleProperty().bind(titles.isNotNull());

    globals.thenAccept(g -> Platform.runLater(() -> items.setAll(g.titles().values().stream().toList())));

    getChildren().addAll(label, choice, clear);
  }

  public Optional<Title> getValue() {
    return Optional.ofNullable(titles.getValue());
  }
}
