package org.tcs.ui.tempo;

import javafx.scene.control.Label;
import org.tcs.backend.Tempo;

public class ListEntry extends Label {
  public final Tempo.Id id;

  public ListEntry(Tempo tempo) {
    setText(tempo.name());
    this.id = tempo.id();
  }
}
