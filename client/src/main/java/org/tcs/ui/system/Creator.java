package org.tcs.ui.system;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.TournamentSystem;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.StringInput;

public class Creator extends VBox {
  private Runnable onBack = () -> {};

  public Creator(Backend backend) {
    var name = new StringInput("Name");

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.createTournamentSystem(new TournamentSystem.Data(name.getValue())).thenAccept(err -> {
        if (err == null) {
          status.setText("");
          onBack.run();
        } else {
          status.setText("Error: " + err);
        }
      });
    }, () -> onBack.run());

    getChildren().addAll(name, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
