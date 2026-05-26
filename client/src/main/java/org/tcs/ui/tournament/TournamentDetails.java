package org.tcs.ui.tournament;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Tournament;

public class TournamentDetails extends VBox {
  private final SimpleStringProperty name = new SimpleStringProperty();
  private final SimpleStringProperty city = new SimpleStringProperty();
  private final SimpleStringProperty start = new SimpleStringProperty();
  private final SimpleStringProperty end = new SimpleStringProperty();
  private final SimpleStringProperty address = new SimpleStringProperty();
  private final SimpleStringProperty tempo = new SimpleStringProperty();
  private final SimpleStringProperty system = new SimpleStringProperty();
  private final SimpleStringProperty organiser = new SimpleStringProperty();
  private final SimpleStringProperty mainArbiter = new SimpleStringProperty();

  private Runnable onBack;

  public TournamentDetails() {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onBack.run());

    var nameLabel = new Label();
    nameLabel.textProperty().bind(name.map(v -> "Name: " + v));
    getChildren().add(nameLabel);

    var cityLabel = new Label();
    cityLabel.textProperty().bind(city.map(v -> "City: " + v));
    getChildren().add(cityLabel);

    var startLabel = new Label();
    startLabel.textProperty().bind(start.map(v -> "Start: " + v));
    getChildren().add(startLabel);

    var endLabel = new Label();
    endLabel.textProperty().bind(end.map(v -> "End: " + v));
    getChildren().add(endLabel);

    var tempoLabel = new Label();
    tempoLabel.textProperty().bind(tempo.map(v -> "Tempo: " + v));
    getChildren().add(tempoLabel);

    var systemLabel = new Label();
    systemLabel.textProperty().bind(system.map(v -> "System: " + v));
    getChildren().add(systemLabel);

    var addressLabel = new Label();
    addressLabel.textProperty().bind(address.map(v -> "Address: " + v));
    getChildren().add(addressLabel);

    var organiserLabel = new Label("Organiser: ");
    var organiserLink = new Hyperlink();
    organiserLink.textProperty().bind(organiser);
    organiserLabel.setLabelFor(organiserLink);
    getChildren().add(inline(organiserLabel, organiserLink));

    var mainArbiterLabel = new Label("Main Arbiter: ");
    var mainArbiterLink = new Hyperlink();
    mainArbiterLink.textProperty().bind(mainArbiter);
    mainArbiterLabel.setLabelFor(mainArbiterLink);
    getChildren().add(inline(mainArbiterLabel, mainArbiterLink));
  }

  public void setTournament(Tournament tournament, Globals globals) {
    name.set(tournament.name());
    city.set(globals.city(tournament.city()).name());
    start.set(tournament.start().toLocalDateTime().toString());
    end.set(tournament.start().toLocalDateTime().toString());
    address.set(tournament.address());
    tempo.set(globals.tempo(tournament.tempo()).name());
    system.set(globals.tournamentSystem(tournament.system()).name());
    organiser.set(tournament.organiser().toString());
    mainArbiter.set(tournament.mainArbiter().toString());
  }

  public void setOnBack(Runnable action) {
    onBack = action;
  }

  private static HBox inline(Node... children) {
    var res = new HBox(children);
    res.setAlignment(Pos.CENTER_LEFT);
    return res;
  }
}
