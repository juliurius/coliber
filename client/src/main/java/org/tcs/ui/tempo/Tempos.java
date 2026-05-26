package org.tcs.ui.tempo;

import javafx.scene.layout.BorderPane;
import org.tcs.backend.Backend;

public class Tempos extends BorderPane {
  public Tempos(Backend backend) {
    var list = new TemposList(backend);

    setCenter(list);
  }
}
