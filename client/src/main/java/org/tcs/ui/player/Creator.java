package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.StringInput;

public class Creator extends VBox {
  private Runnable onBack = () -> {};

  public Creator(Backend backend) {
    var name = new StringInput("Name");
    var surname = new StringInput("Surname");

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.createPlayer(new Player.Data(name.getValue(), surname.getValue())).thenAccept(err -> Platform.runLater(() -> {
        if (err == null) {
          status.setText("");
          onBack.run();
        } else {
          status.setText("Error: " + err);
        }
      }));
    }, () -> onBack.run());

    getChildren().addAll(name, surname, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
