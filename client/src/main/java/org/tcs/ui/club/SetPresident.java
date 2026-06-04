package org.tcs.ui.club;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.PlayerInput;

public class SetPresident extends VBox {
  private Runnable onBack = () -> {};

  public SetPresident(Backend backend, Club.Id clubId) {
    var player = new PlayerInput(() -> backend.getClubMembers(clubId), "President");

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.setClubPresident(clubId, player.getValue()).thenAccept(err -> showResult(err, status));
    }, () -> onBack.run());

    var clear = new Button("Clear President");
    clear.setOnAction(_ -> {
      status.setText("Wait...");
      backend.setClubPresident(clubId, null).thenAccept(err -> showResult(err, status));
    });

    getChildren().addAll(player, buttons, clear, status);
  }

  private void showResult(String err, Text status) {
    Platform.runLater(() -> {
      if (err == null) {
        status.setText("");
        onBack.run();
      } else {
        status.setText("Error: " + err);
      }
    });
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
