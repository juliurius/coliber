package org.tcs.ui.player;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.Globals;
import org.tcs.backend.ArbiterClass;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.ui.util.ArbiterClassInput;
import org.tcs.ui.util.FormButtons;

import java.util.concurrent.CompletableFuture;

public class SetArbiterClass extends VBox {
  private Runnable onBack = () -> {};

  public SetArbiterClass(Backend backend, Player.Id player, CompletableFuture<Globals> globals) {
    var arbiterClass = new ArbiterClassInput(globals);

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.setPlayerArbiterClass(player, arbiterClass.getValue().map(ArbiterClass::id).orElse(null)).thenAccept(err -> {
        if (err == null) {
          status.setText("");
          onBack.run();
        } else {
          status.setText("Error: " + err);
        }
      });
    }, () -> onBack.run());

    getChildren().addAll(arbiterClass, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}