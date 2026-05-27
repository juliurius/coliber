package org.tcs.ui.player;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.tcs.backend.Player;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;

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

    var clubLabel = new Label("Club: ");
    var clubLink = new Hyperlink();
    clubLink.setOnAction(_ -> {
      if (player.get().club() == null) return;
      onNav.get().accept(new Nav.Club(player.get().club().id()));
    });
    clubLink.textProperty().bind(player.map(v -> v.club() == null ? "None" : v.club().name()));
    clubLink.disableProperty().bind(player.map(v -> v.club() == null));
    clubLabel.setLabelFor(clubLink);
    getChildren().add(Util.inline(clubLabel, clubLink));
  }

  public ObjectProperty<Player> playerProperty() {
    return player;
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }
}
