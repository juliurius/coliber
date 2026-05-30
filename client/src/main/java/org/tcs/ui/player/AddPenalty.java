package org.tcs.ui.player;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.backend.Backend;
import org.tcs.backend.Penalty;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerFilter;
import org.tcs.ui.util.*;

public class AddPenalty extends VBox {
  private Runnable onBack = () -> {};

  public AddPenalty(Backend backend, Player.Id player) {
    var until = new DateInput("Until");
    var reason = new StringInput("Reason");
    var tournament = new TournamentInput(backend);
    var arbiter = new PlayerInput(backend, new PlayerFilter(true));

    var status = new Text();
    var buttons =
        new FormButtons(
            () -> {
              status.setText("Wait...");
              backend
                  .addPlayerPenalty(
                      player,
                      new Penalty.Data(
                          until.getValue(),
                          reason.getValue(),
                          tournament.getValue(),
                          arbiter.getValue()))
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

    getChildren().addAll(until, reason, arbiter, tournament, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
