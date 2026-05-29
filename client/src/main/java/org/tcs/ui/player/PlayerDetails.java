package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Norm;
import org.tcs.backend.Penalty;
import org.tcs.backend.Player;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;

import java.util.function.Consumer;

public class PlayerDetails extends VBox {
  private final SimpleObjectProperty<Player> player = new SimpleObjectProperty<>();
  private final ObjectProperty<Globals> globals = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public PlayerDetails(Backend backend) {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Player.All()));

    var nameLabel = new Label();
    nameLabel.textProperty().bind(player.map(v -> "Name: " + v.name()));
    getChildren().add(nameLabel);

    var surnameLabel = new Label();
    surnameLabel.textProperty().bind(player.map(v -> "Surname: " + v.surname()));
    getChildren().add(surnameLabel);

    var ratingLabel = new Label();
    ratingLabel.textProperty().bind(player.map(v -> "Rating: " + v.rating()));
    getChildren().add(ratingLabel);

    var playerClassLabel = new Label();
    playerClassLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "";
                  return "Player Class: "
                      + (player.get().playerClass() == null
                          ? "None"
                          : globals.get().playerClass(player.get().playerClass()).name());
                },
                player,
                globals));
    getChildren().add(playerClassLabel);

    var arbiterClassLabel = new Label();
    arbiterClassLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "";
                  return "Arbiter Class: "
                      + (player.get().arbiterClass() == null
                          ? "None"
                          : globals.get().arbiterClass(player.get().arbiterClass()).name());
                },
                player,
                globals));
    getChildren().add(arbiterClassLabel);

    var titleLabel = new Label();
    titleLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "";
                  return "Title: "
                      + (player.get().title() == null
                          ? "None"
                          : globals.get().title(player.get().title()).name());
                },
                player,
                globals));
    getChildren().add(titleLabel);

    var clubLabel = new Label("Club: ");
    var clubLink = new Hyperlink();
    clubLink.setOnAction(
        _ -> {
          if (player.get().club() == null) return;
          onNav.get().accept(new Nav.Club(player.get().club().id()));
        });
    clubLink.textProperty().bind(player.map(v -> v.club() == null ? "None" : v.club().name()));
    clubLink.disableProperty().bind(player.map(v -> v.club() == null));
    clubLabel.setLabelFor(clubLink);
    getChildren().add(Util.inline(clubLabel, clubLink));

    var penaltiesLabel = new Label("Penalties");
    var penalties = new TableView<Penalty>();

    var untilColumn = new TableColumn<Penalty, String>("Until");
    untilColumn.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().until().toLocalDate().toString()));
    penalties.getColumns().add(untilColumn);

    var reasonColumn = new TableColumn<Penalty, String>("Reason");
    reasonColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().reason()));
    penalties.getColumns().add(reasonColumn);

    var arbiterColumn = new TableColumn<Penalty, String>("Arbiter");
    arbiterColumn.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().arbiter().toString()));
    penalties.getColumns().add(arbiterColumn);

    player.addListener(
        _ ->
            backend
                .getPlayerPenalties(player.get().id())
                .thenAccept(v -> Platform.runLater(() -> penalties.getItems().setAll(v))));

    getChildren().addAll(penaltiesLabel, penalties);

    var normsLabel = new Label("Norms");
    var norms = new TableView<Norm>();

    var titleColumn = new TableColumn<Norm, String>("Title");
    titleColumn.setCellValueFactory(
      p -> Bindings.createStringBinding(() -> {
        if (globals.get() == null) return "";
        return globals.get().title(p.getValue().title()).name();
      }, globals));
    titleColumn.setMinWidth(200);
    norms.getColumns().add(titleColumn);

    var tournamentColumn = new TableColumn<Norm, String>("Tournament");
    tournamentColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().tournament().name()));
    tournamentColumn.setMinWidth(400);
    norms.getColumns().add(tournamentColumn);

    player.addListener(
      _ ->
        backend
          .getPlayerNorms(player.get().id())
          .thenAccept(v -> Platform.runLater(() -> norms.getItems().setAll(v))));

    getChildren().addAll(normsLabel, norms);
  }

  public ObjectProperty<Player> playerProperty() {
    return player;
  }

  public ObjectProperty<Globals> globalsProperty() {
    return globals;
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }
}
