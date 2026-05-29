package org.tcs.ui.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.tcs.Globals;
import org.tcs.backend.City;

import java.util.concurrent.CompletableFuture;

public class CityInput extends HBox {
  private final SimpleObjectProperty<City> city = new SimpleObjectProperty<>();

  public CityInput(CompletableFuture<Globals> globals) {
    var items = FXCollections.<City>observableArrayList();
    var label = new Label("City: ");
    var choice = new ComboBox<City>();
    choice.setItems(items);
    choice.valueProperty().bindBidirectional(city);
    choice.setPromptText("-");
    choice.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(City item, boolean empty) {
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
      protected void updateItem(City item, boolean empty) {
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
    clear.setOnAction(_ -> city.set(null));
    clear.visibleProperty().bind(city.isNotNull());

    globals.thenAccept(g -> Platform.runLater(() -> items.setAll(g.cities().values().stream().toList())));

    getChildren().addAll(label, choice, clear);
  }

  public City getValue() {
    return city.getValue();
  }
}
