package org.tcs.ui.system;

import javafx.scene.layout.BorderPane;
import org.tcs.backend.Backend;

public class Systems extends BorderPane {
  public Systems(Backend backend) {
    var list = new SystemsList(backend);

    setCenter(list);
  }
}
