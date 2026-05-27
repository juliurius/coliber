package org.tcs.ui.tempo;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.tcs.backend.Tempo;

public class ListEntry extends HBox {
  public ListEntry(Tempo tempo) {
    var name = new Label(tempo.name() == null ? "No name" : tempo.name());
    name.setMinWidth(256.0);
    getChildren().add(name);
    getChildren().add(new Text(tempo.description() == null ? "-" : tempo.description()));
  }
}
