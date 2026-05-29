package org.tcs.ui.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class StringInput extends HBox {
  private final SimpleStringProperty value = new SimpleStringProperty("");

  public StringInput(String label) {
    var labelNode = new Label(label + ": ");
    var field = new TextField();
    field.textProperty().bindBidirectional(value);
    labelNode.setLabelFor(field);

    getChildren().addAll(labelNode, field);
  }

  public String getValue() {
    return value.getValue();
  }
}
