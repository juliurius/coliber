package org.tcs.ui.club;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.ui.util.CityInput;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.StringInput;

import java.util.concurrent.CompletableFuture;

public class Creator extends VBox {
  private Runnable onBack = () -> {};

  public Creator(Backend backend, CompletableFuture<Globals> globals) {
    var name = new StringInput("Name");
    var city = new CityInput(globals);

    var status = new Text();
    var buttons = new FormButtons(() -> {
      status.setText("Wait...");
      backend.createClub(new Club.Data(name.getValue(), city.getValue().id())).thenAccept(err -> {
        if (err == null) {
          status.setText("");
          onBack.run();
        } else {
          status.setText("Error: " + err);
        }
      });
    }, () -> onBack.run());

    getChildren().addAll(name, city, buttons, status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }
}
