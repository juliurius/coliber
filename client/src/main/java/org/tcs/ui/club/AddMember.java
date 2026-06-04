package org.tcs.ui.club;

import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.PlayerInput;

public class AddMember extends VBox {
  private Runnable onBack = () -> {};

  public AddMember(Backend backend, Club.Id clubId) {
    var player = new PlayerInput(backend, "Member to add");

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.addClubMember(clubId, player.getValue()).thenAccept(err -> Platform.runLater(() -> {
        if (err == null) {
          status.setText("");
          onBack.run();
        } else {
          status.setText("Error: " + err);
        }
      }));
    }, () -> onBack.run());

    getChildren().addAll(player, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
