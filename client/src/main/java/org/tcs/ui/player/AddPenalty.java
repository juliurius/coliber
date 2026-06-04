package org.tcs.ui.player;

import java.util.concurrent.CompletableFuture;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Penalty;
import org.tcs.backend.PenaltyRoleContext;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerFilter;
import org.tcs.ui.util.*;

public class AddPenalty extends VBox {
  private Runnable onBack = () -> {};

  public AddPenalty(Backend backend, Player.Id player, CompletableFuture<Globals> globals) {
    var until = new DateInput("Penalty until");
    var reason = new StringInput("Penalty reason");
    var roleContext = new PenaltyRoleContextInput(globals);
    var tournament = new TournamentInput(backend, "Related tournament");
    var arbiter = new PlayerInput(backend, PlayerFilter.ARBITERS_ONLY, "Arbiter");

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
                          arbiter.getValue(),
                          roleContext.getValue().map(PenaltyRoleContext::id).orElse(null)))
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

    getChildren().addAll(until, reason, roleContext, arbiter, tournament, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
