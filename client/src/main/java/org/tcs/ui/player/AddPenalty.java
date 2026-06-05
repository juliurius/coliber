package org.tcs.ui.player;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Penalty;
import org.tcs.backend.PenaltyRoleContext;
import org.tcs.backend.Player;
import org.tcs.ui.util.*;

public class AddPenalty extends VBox {
  private Runnable onBack = () -> {};

  public AddPenalty(Backend backend, Player.Id player, CompletableFuture<Globals> globals) {
    var until = new DateInput("Penalty until (empty = lifetime)");
    var reason = new StringInput("Penalty reason");
    var roleContext = new PenaltyRoleContextInput(globals);
    var tournament = new TournamentInput(backend.getPlayerPenaltyTournaments(player), "Related tournament");
    var arbiter = new PlayerInput(() -> CompletableFuture.completedFuture(List.of()), "Arbiter");

    tournament.valueProperty().addListener((_, _, id) -> {
      if (id == null) {
        arbiter.setPlayers(List.of());
      } else {
        backend
            .getTournamentArbiters(id)
            .thenAccept(arbiters -> Platform.runLater(() -> arbiter.setPlayers(arbiters)));
      }
    });

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
                      err -> Platform.runLater(() -> {
                        if (err == null) {
                          status.setText("");
                          onBack.run();
                        } else {
                          status.setText("Error: " + err);
                        }
                      }));
            },
            () -> onBack.run());

    getChildren().addAll(until, reason, roleContext, tournament, arbiter, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
