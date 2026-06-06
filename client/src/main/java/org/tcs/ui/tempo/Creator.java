package org.tcs.ui.tempo;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.Tempo;
import org.tcs.ui.Util;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.StringInput;

public class Creator extends VBox {
  private Runnable onBack = () -> {};

  public Creator(Backend backend) {
    var name = new ComboBox<String>();
    name.getItems().addAll("Klasyczne", "Szybkie", "Błyskawiczne");
    name.setValue("Klasyczne");
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

    getChildren().addAll(Util.inline(new Label("Name: "), name), description, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
