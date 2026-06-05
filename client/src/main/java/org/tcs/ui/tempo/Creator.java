package org.tcs.ui.tempo;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.Tempo;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.StringInput;

public class Creator extends VBox {
  private Runnable onBack = () -> {};

  public Creator(Backend backend) {
    var name = new StringInput("Name");
    var description = new StringInput("Description");

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.createTempo(new Tempo.Data(name.getValue(), description.getValue())).thenAccept(err -> Platform.runLater(() -> {
        if (err == null) {
          status.setText("");
          onBack.run();
        } else {
          status.setText("Error: " + err);
        }
      }));
    }, () -> onBack.run());

    getChildren().addAll(name, description, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
