package org.tcs.ui.player;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.tcs.backend.Player;
import org.tcs.ui.Nav;

import java.util.function.Consumer;

public class PlayerDetails extends VBox {
  private final SimpleObjectProperty<Player> player = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public PlayerDetails() {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Player(null)));

    var nameLabel = new Label();
    nameLabel.textProperty().bind(player.map(v -> "Name: " + v.name()));
    getChildren().add(nameLabel);

    var surnameLabel = new Label();
    surnameLabel.textProperty().bind(player.map(v -> "Surname: " + v.surname()));
    getChildren().add(surnameLabel);

    var ratingLabel = new Label();
    ratingLabel.textProperty().bind(player.map(v -> "Rating: " + v.rating()));
    getChildren().add(ratingLabel);
  }

  public ObjectProperty<Player> playerProperty() {
    return player;
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }
}
