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
import org.tcs.backend.PenaltyRoleContext;

public class PenaltyRoleContextInput extends HBox {
  private final SimpleObjectProperty<PenaltyRoleContext> roleContext = new SimpleObjectProperty<>();

  public PenaltyRoleContextInput(CompletableFuture<Globals> globals) {
    var items = FXCollections.<PenaltyRoleContext>observableArrayList();
    var label = new Label("Role: ");
    var choice = new ComboBox<PenaltyRoleContext>();
    choice.setItems(items);
    choice.valueProperty().bindBidirectional(roleContext);
    choice.setPromptText("-");
    choice.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(PenaltyRoleContext item, boolean empty) {
        super.updateItem(item, empty);
        setText(item == null || empty ? "-" : item.name());
      }
    });
    choice.setCellFactory(_ -> new ListCell<>() {
      @Override
      protected void updateItem(PenaltyRoleContext item, boolean empty) {
        super.updateItem(item, empty);
        setText(item == null || empty ? "-" : item.name());
      }
    });
    label.setLabelFor(choice);

    var clear = new Button("Clear");
    clear.setOnAction(_ -> roleContext.set(null));
    clear.visibleProperty().bind(roleContext.isNotNull());

    globals.thenAccept(
        g -> Platform.runLater(() -> items.setAll(g.penaltyRoleContexts().values().stream().toList())));

    getChildren().addAll(label, choice, clear);
  }

  public Optional<PenaltyRoleContext> getValue() {
    return Optional.ofNullable(roleContext.getValue());
  }
}
