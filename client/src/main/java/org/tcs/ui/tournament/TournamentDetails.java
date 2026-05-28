package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Tournament;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;
import org.tcs.ui.player.PlayerDataEntry;

import java.util.function.Consumer;

public class TournamentDetails extends VBox {
  private final SimpleObjectProperty<Tournament> tournament = new SimpleObjectProperty<>();
  private final ObjectProperty<Globals> globals = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public TournamentDetails(Backend backend) {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Tournament(null)));

    var nameLabel = new Label();
    nameLabel.textProperty().bind(tournament.map(v -> "Name: " + v.name()));
    getChildren().add(nameLabel);

    var cityLabel = new Label();
    cityLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null || tournament.get().city() == null) return "City: None";
                  return "City: " + globals.get().city(tournament.get().city()).name();
                }, tournament, globals));
    getChildren().add(cityLabel);

    var startLabel = new Label();
    startLabel.textProperty().bind(tournament.map(v -> "Start: " + v.start().toLocalDateTime()));
    getChildren().add(startLabel);

    var endLabel = new Label();
    endLabel.textProperty().bind(tournament.map(v -> "End: " + v.end().toLocalDateTime()));
    getChildren().add(endLabel);

    var tempoLabel = new Label();
    tempoLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "Tempo: Unknown";
                  return "Tempo: " + globals.get().tempo(tournament.get().tempo()).name();
                },
                tournament,
                globals));
    getChildren().add(tempoLabel);

    var systemLabel = new Label();
    systemLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "Tournament system: Unknown";
                  return "Tournament system: "
                      + globals.get().tournamentSystem(tournament.get().system()).name();
                },
                tournament,
                globals));
    getChildren().add(systemLabel);

    var addressLabel = new Label();
    addressLabel.textProperty().bind(tournament.map(v -> "Address: " + v.address()));
    getChildren().add(addressLabel);

    var organiserLabel = new Label("Organiser: ");
    var organiserLink = new Hyperlink();
    organiserLink.setOnAction(_ -> onNav.get().accept(new Nav.Player(tournament.get().organiser().id())));
    organiserLink.textProperty().bind(tournament.map(v -> v.organiser().toString()));
    organiserLabel.setLabelFor(organiserLink);
    getChildren().add(Util.inline(organiserLabel, organiserLink));

    var mainArbiterLabel = new Label("Main Arbiter: ");
    var mainArbiterLink = new Hyperlink();
    mainArbiterLink.setOnAction(
        _ -> onNav.get().accept(new Nav.Player(tournament.get().mainArbiter().id())));
    mainArbiterLink.textProperty().bind(tournament.map(v -> v.mainArbiter().toString()));
    mainArbiterLabel.setLabelFor(mainArbiterLink);
    getChildren().add(Util.inline(mainArbiterLabel, mainArbiterLink));

    var tournamentArbitersLabel = new Label("Tournament Arbiters: ");
    var tournamentArbitersList = new ListView<PlayerDataEntry>();
    var items = FXCollections.<PlayerDataEntry>observableArrayList();
    tournamentArbitersList.setItems(items);
    tournamentArbitersLabel.setLabelFor(tournamentArbitersList);
    tournament.addListener(
      _ -> {
        if (tournament.get() == null) return;
        backend
          .getTournamentArbiters(tournament.get().id())
          .thenAccept(
            members -> {
              System.out.println("Got tournament arbiters: " + members);
              Platform.runLater(
                () -> items.setAll(members.stream().map(brief -> {
                  var entry = new PlayerDataEntry(brief);
                  entry.onNavProperty().bind(onNav);
                  return entry;
                }).toList()));
            });
      });
    getChildren().addAll(tournamentArbitersLabel, tournamentArbitersList);
  }

  public ObjectProperty<Tournament> tournamentProperty() {
    return tournament;
  }

  public ObjectProperty<Globals> globalsProperty() {
    return globals;
  }


  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }
}
