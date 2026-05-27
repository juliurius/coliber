package org.tcs.ui.club;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Club;
import org.tcs.ui.Nav;

import java.util.function.Consumer;

public class ClubDetails extends VBox {
  private final SimpleObjectProperty<Club> club = new SimpleObjectProperty<>();
  private final ObjectProperty<Globals> globals = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public ClubDetails() {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Club(null)));

    var nameLabel = new Label();
    nameLabel.textProperty().bind(club.map(v -> "Name: " + v.name()));
    getChildren().add(nameLabel);

    var cityLabel = new Label();
    cityLabel.textProperty().bind(
      Bindings.createStringBinding(
        () -> {
          if (globals.get() == null || club.get().city() == null) return "City: None";
          return "City: " + globals.get().city(club.get().city()).name();
        }, club, globals));
    getChildren().add(cityLabel);

    var presidentLabel = new Label("President: ");
    var presidentLink = new Hyperlink();
    presidentLink.setOnAction(_ -> {
      if (club.get().president() == null) return;
      onNav.get().accept(new Nav.Player(club.get().president().id()));
    });
    presidentLink.textProperty().bind(club.map(v -> v.president() == null ? "None" : v.president().toString()));
    presidentLink.disableProperty().bind(club.map(v -> v.president() == null));
    presidentLabel.setLabelFor(presidentLink);
    getChildren().add(inline(presidentLabel, presidentLink));
  }

  public ObjectProperty<Club> clubProperty() {
    return club;
  }

  public ObjectProperty<Globals> globalsProperty() {
    return globals;
  }


  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  private static HBox inline(Node... children) {
    var res = new HBox(children);
    res.setAlignment(Pos.CENTER_LEFT);
    return res;
  }
}
