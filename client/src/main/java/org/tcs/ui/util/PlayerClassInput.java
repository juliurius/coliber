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
import org.tcs.backend.PlayerClass;

public class PlayerClassInput extends HBox {
  private final SimpleObjectProperty<PlayerClass> classes = new SimpleObjectProperty<>();

  public PlayerClassInput(CompletableFuture<Globals> globals) {
    var items = FXCollections.<PlayerClass>observableArrayList();
    var label = new Label("Player Class: ");
    var choice = new ComboBox<PlayerClass>();
    choice.setItems(items);
    choice.valueProperty().bindBidirectional(classes);
    choice.setPromptText("-");
    choice.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(PlayerClass item, boolean empty) {
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
      protected void updateItem(PlayerClass item, boolean empty) {
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
    clear.setOnAction(_ -> classes.set(null));
    clear.visibleProperty().bind(classes.isNotNull());

    globals.thenAccept(g -> Platform.runLater(() -> items.setAll(g.playerClasses().values().stream().toList())));

    getChildren().addAll(label, choice, clear);
  }

  public Optional<PlayerClass> getValue() {
    return Optional.ofNullable(classes.getValue());
  }
}
