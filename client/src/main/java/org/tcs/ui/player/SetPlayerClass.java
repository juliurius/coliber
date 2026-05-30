package org.tcs.ui.player;

import java.util.concurrent.CompletableFuture;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerClass;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.PlayerClassInput;
import org.tcs.ui.util.TournamentInput;

public class SetPlayerClass extends VBox {
  private Runnable onBack = () -> {};

  public SetPlayerClass(Backend backend, Player.Id player, CompletableFuture<Globals> globals) {
    var playerClass = new PlayerClassInput(globals);
    var tournament = new TournamentInput(backend);

    var status = new Text();
    var buttons =
        new FormButtons(
            () -> {
              status.setText("Wait...");
              backend
                  .setPlayerPlayerClass(
                      player,
                      playerClass.getValue().map(PlayerClass::id).orElse(null),
                      tournament.getValue())
                  .thenAccept(
                      err -> {
                        if (err == null) {
                          status.setText("");
                          onBack.run();
                        } else {
                          status.setText("Error: " + err);
                        }
                      });
            },
            () -> onBack.run());

    getChildren().addAll(playerClass, tournament, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}