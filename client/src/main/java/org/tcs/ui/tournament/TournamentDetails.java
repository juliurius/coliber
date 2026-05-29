package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.*;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;
import org.tcs.ui.player.PlayerDataEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

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
                },
                tournament,
                globals));
    getChildren().add(cityLabel);

    var startLabel = new Label();
    startLabel.textProperty().bind(tournament.map(v -> "Start: " + v.start().toLocalDateTime()));
    getChildren().add(startLabel);

    var endLabel = new Label();
    endLabel.textProperty().bind(tournament.map(v -> "End: " + v.start().toLocalDateTime()));
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
    organiserLink.setOnAction(
        _ -> onNav.get().accept(new Nav.Player.Details(tournament.get().organiser().id())));
    organiserLink.textProperty().bind(tournament.map(v -> v.organiser().toString()));
    organiserLabel.setLabelFor(organiserLink);
    getChildren().add(Util.inline(organiserLabel, organiserLink));

    var mainArbiterLabel = new Label("Main Arbiter: ");
    var mainArbiterLink = new Hyperlink();
    mainArbiterLink.setOnAction(
        _ -> onNav.get().accept(new Nav.Player.Details(tournament.get().mainArbiter().id())));
    mainArbiterLink.textProperty().bind(tournament.map(v -> v.mainArbiter().toString()));
    mainArbiterLabel.setLabelFor(mainArbiterLink);
    getChildren().add(Util.inline(mainArbiterLabel, mainArbiterLink));

    players("Arbiters: ", () -> backend.getTournamentArbiters(tournament.get().id()));
    players("Players: ", () -> backend.getTournamentPlayers(tournament.get().id()));

    var currentRound = new SimpleObjectProperty<Round.Id>();
    var roundButtons = new HBox();
    roundButtons.setMaxHeight(Region.USE_PREF_SIZE);
    getChildren().add(roundButtons);

    rounds(currentRound, backend);

    tournament.addListener(
      _ -> {
        if (tournament.get() == null) return;

        backend
          .getTournamentRounds(tournament.get().id())
          .thenAccept(
            rounds ->
              Platform.runLater(
                () -> {
                  var buttons = new ArrayList<Button>();

                  for (int i = 0; i < rounds.size(); i++) {
                    var btn = new Button(Integer.toString(i + 1));
                    Round round = rounds.get(i);
                    btn.setOnAction(_ -> currentRound.set(round.id()));
                    buttons.add(btn);
                  }

                  roundButtons.getChildren().setAll(buttons);
                  if (rounds.isEmpty()) return;
                  currentRound.set(rounds.getFirst().id());
                }));
      });
  }

  private void players(String text, Supplier<CompletableFuture<List<PlayerBrief>>> players) {
    var label = new Label(text);
    var list = new ListView<PlayerDataEntry>();
    var items = FXCollections.<PlayerDataEntry>observableArrayList();
    list.setItems(items);
    label.setLabelFor(list);
    tournament.addListener(
        _ -> {
          if (tournament.get() == null) return;
          players
              .get()
              .thenAccept(
                  members ->
                      Platform.runLater(
                          () ->
                              items.setAll(
                                  members.stream()
                                      .map(
                                          brief -> {
                                            var entry = new PlayerDataEntry(brief);
                                            entry.onNavProperty().bind(onNav);
                                            return entry;
                                          })
                                      .toList())));
        });
    getChildren().addAll(label, list);
  }

  private void rounds(ObservableObjectValue<Round.Id> currentRound, Backend backend) {
    var rounds = new TableView<Game>();

    var white = new TableColumn<Game, String>("White");
    white.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().white().toString()));
    white.setMinWidth(200);
    rounds.getColumns().add(white);

    var whiteScore = new TableColumn<Game, String>("Score");
    whiteScore.setCellValueFactory(
        p ->
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "";
                  Game game = p.getValue();
                  if (game.over() == null) return "";

                  GameOverReason reason = globals.get().gameOverReason(game.over().reason());
                  return Float.toString(
                      game.over().whiteWon() ? reason.winScore() : reason.loseScore());
                },
                globals));
    whiteScore.setMaxWidth(50);
    rounds.getColumns().add(whiteScore);

    var whiteRating = new TableColumn<Game, String>("Rating");
    whiteRating.setCellValueFactory(
      p ->
        Bindings.createStringBinding(
          () -> {
            if (globals.get() == null) return "";
            Game game = p.getValue();
            if (game.over() == null) return "";

            return String.format("%+d", game.over().whiteRatingChange());
          },
          globals));
    whiteRating.setMaxWidth(70);
    rounds.getColumns().add(whiteRating);

    var black = new TableColumn<Game, String>("Black");
    black.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().black().toString()));
    black.setMinWidth(200);
    rounds.getColumns().add(black);

    var blackScore = new TableColumn<Game, String>("Score");
    blackScore.setCellValueFactory(
        p ->
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "";
                  Game game = p.getValue();
                  if (game.over() == null) return "";

                  GameOverReason reason = globals.get().gameOverReason(game.over().reason());
                  return Float.toString(
                      game.over().whiteWon() ? reason.loseScore() : reason.winScore());
                },
                globals));
    blackScore.setMaxWidth(50);
    rounds.getColumns().add(blackScore);

    var blackRating = new TableColumn<Game, String>("Rating");
    blackRating.setCellValueFactory(
      p ->
        Bindings.createStringBinding(
          () -> {
            if (globals.get() == null) return "";
            Game game = p.getValue();
            if (game.over() == null) return "";

            return String.format("%+d", game.over().blackRatingChange());
          },
          globals));
    blackRating.setMaxWidth(70);
    rounds.getColumns().add(blackRating);

    var reasonColumn = new TableColumn<Game, String>("Reason");
    reasonColumn.setCellValueFactory(
        p ->
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "";
                  Game game = p.getValue();
                  if (game.over() == null) return "";

                  GameOverReason reason = globals.get().gameOverReason(game.over().reason());
                  return reason.description();
                },
                globals));
    rounds.getColumns().add(reasonColumn);

    var arbiter = new TableColumn<Game, String>("Arbiter");
    arbiter.setCellValueFactory(
        p ->
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "";
                  Game game = p.getValue();
                  if (game.over() == null) return "";

                  return game.over().arbiter().toString();
                },
                globals));
    arbiter.setMinWidth(200);
    rounds.getColumns().add(arbiter);

    currentRound.addListener(
        _ ->
            backend
                .getRoundGames(currentRound.get())
                .thenAccept(v -> Platform.runLater(() -> rounds.getItems().setAll(v))));

    getChildren().addAll(rounds);
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
