package org.tcs.ui.club;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Club;

public class ClubDetails extends VBox {
  private final SimpleStringProperty name = new SimpleStringProperty();
  private final SimpleStringProperty city = new SimpleStringProperty();
  private final SimpleStringProperty president = new SimpleStringProperty();

  private Runnable onBack;

  public ClubDetails() {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onBack.run());

    var nameLabel = new Label();
    nameLabel.textProperty().bind(name.map(v -> "Name: " + v));
    getChildren().add(nameLabel);

    var cityLabel = new Label();
    cityLabel.textProperty().bind(city.map(v -> "City: " + v));
    getChildren().add(cityLabel);

    var presidentLabel = new Label();
    presidentLabel.textProperty().bind(president.map(v -> "President: " + v));
    getChildren().add(presidentLabel);
  }

  public void setClub(Club player, Globals globals) {
    name.set(player.name());
    city.set(globals.city(player.city()).name());
    president.set(player.president().toString());
  }

  public void setOnBack(Runnable action) {
    onBack = action;
  }
}
