package org.tcs.ui.util;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.sql.Date;
import java.time.LocalDate;

public class DateInput extends HBox {
  private final ObjectProperty<LocalDate> value = new SimpleObjectProperty<>(LocalDate.now());

  public DateInput(String label) {
    var labelNode = new Label(label + ": ");
    var field = new DatePicker();
    field.valueProperty().bindBidirectional(value);
    labelNode.setLabelFor(field);

    getChildren().addAll(labelNode, field);
  }

  public Date getValue() {
    return Date.valueOf(value.getValue());
  }
}
