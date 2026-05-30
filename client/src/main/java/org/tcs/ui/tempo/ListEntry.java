package org.tcs.ui.tempo;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.tcs.backend.Tempo;
import org.tcs.ui.util.SimpleListEntry;

public class ListEntry extends HBox implements SimpleListEntry<Tempo> {
  private final Tempo tempo;

  public ListEntry(Tempo tempo) {
    this.tempo = tempo;
    var name = new Label(tempo.name() == null ? "No name" : tempo.name());
    name.setMinWidth(256.0);
    getChildren().add(name);
    getChildren().add(new Text(tempo.description() == null ? "-" : tempo.description()));
  }

  @Override
  public Tempo id() {
    return tempo;
  }
}
