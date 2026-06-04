package org.tcs.ui.player;

import java.util.concurrent.CompletableFuture;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Player;
import org.tcs.backend.Title;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.TitleInput;
import org.tcs.ui.util.TournamentInput;

public class SetTitle extends VBox {
  private Runnable onBack = () -> {};

  public SetTitle(Backend backend, Player.Id player, CompletableFuture<Globals> globals) {
    var title = new TitleInput(globals);
    var tournament = new TournamentInput(backend, "Awarding tournament");

    var status = new Text();
    var buttons =
        new FormButtons(
            () -> {
              status.setText("Wait...");
              backend
                  .setPlayerTitle(
                      player,
                      title.getValue().map(Title::id).orElse(null),
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

    getChildren().addAll(title, tournament, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
