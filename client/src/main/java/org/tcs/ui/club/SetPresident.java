package org.tcs.ui.club;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.PlayerInput;

public class SetPresident extends VBox {
  private Runnable onBack = () -> {};

  public SetPresident(Backend backend, Club.Id clubId) {
    var player = new PlayerInput(backend);

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.setClubPresident(clubId, player.getValue()).thenAccept(err -> {
        if (err == null) {
          status.setText("");
          onBack.run();
        } else {
          status.setText("Error: " + err);
        }
      });
    }, () -> onBack.run());

    getChildren().addAll(player, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}