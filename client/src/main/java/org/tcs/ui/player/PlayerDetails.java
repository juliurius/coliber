package org.tcs.ui.player;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.tcs.backend.Player;

public class PlayerDetails extends VBox {
  private final SimpleStringProperty name = new SimpleStringProperty();
  private final SimpleStringProperty surname = new SimpleStringProperty();
  private final SimpleIntegerProperty rating = new SimpleIntegerProperty();

  private Runnable onBack;

  public PlayerDetails() {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onBack.run());

    var nameLabel = new Label();
    nameLabel.textProperty().bind(name.map(v -> "Name: " + v));
    getChildren().add(nameLabel);

    var surnameLabel = new Label();
    surnameLabel.textProperty().bind(surname.map(v -> "Surname: " + v));
    getChildren().add(surnameLabel);

    var ratingLabel = new Label();
    ratingLabel.textProperty().bind(rating.map(v -> "Rating: " + v));
    getChildren().add(ratingLabel);
  }

  public void setPlayer(Player player) {
    name.set(player.name());
    surname.set(player.surname());
    rating.set(player.rating());
  }

  public void setOnBack(Runnable action) {
    onBack = action;
  }
}
