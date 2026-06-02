package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.tcs.Globals;
import org.tcs.backend.*;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;
import org.tcs.ui.player.PlayerDataEntry;
import org.tcs.ui.util.PlayerInput;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class TournamentDetails extends VBox {
  private final SimpleObjectProperty<Tournament> tournament = new SimpleObjectProperty<>();
  private final ObjectProperty<Globals> globals = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public TournamentDetails(Backend backend) {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Tournament.All()));

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
    endLabel.textProperty().bind(tournament.map(v -> "End: " + v.end().toLocalDateTime()));
    getChildren().add(endLabel);

    var tempoLabel = new Label();
    tempoLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null) return "Tempo: Unknown";
                  return "Tempo: " + tournament.get().tempo().name();
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
                      + tournament.get().system().name();
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

    players("Arbiters: ",
            "Select arbiter",
            () -> backend.getTournamentArbiters(tournament.get().id()),
            (playerId) -> backend.addTournamentArbiter(tournament.get().id(), playerId),
            backend);
    players("Players: ",
            "Select player",
            () -> backend.getTournamentPlayers(tournament.get().id()),
            (playerId) -> backend.addTournamentPlayer(tournament.get().id(), playerId),
            backend);

    var currentRound = new SimpleObjectProperty<Round.Id>();
    var tournamentClosed = new SimpleBooleanProperty(false);
    var roundLimitReached = new SimpleBooleanProperty(false);
    var roundButtons = new HBox();
    roundButtons.setMaxHeight(Region.USE_PREF_SIZE);
    getChildren().add(roundButtons);

    var standings = standingsTable();
    var standingsRound = new SimpleObjectProperty<Round.Id>();
    var standingsButtons = new HBox();
    standingsButtons.setMaxHeight(Region.USE_PREF_SIZE);

    Consumer<Round.Id> loadStandingsFor = roundId -> {
      if (tournament.get() == null || roundId == null) {
        standings.getItems().clear();
        return;
      }
      backend
          .getTournamentStandings(tournament.get().id(), roundId)
          .thenAccept(v -> Platform.runLater(() -> standings.getItems().setAll(v)));
    };
    standingsRound.addListener((_, _, sel) -> loadStandingsFor.accept(sel));

    Runnable reloadStandings = () -> {
      if (tournament.get() == null) return;
      Round.Id previous = standingsRound.get();
      backend
          .getTournamentRounds(tournament.get().id())
          .thenAccept(rs -> Platform.runLater(() -> {
            var buttons = new ArrayList<Button>();
            for (int i = 0; i < rs.size(); i++) {
              Round round = rs.get(i);
              var btn = new Button(Integer.toString(i + 1));
              btn.setOnAction(_ -> standingsRound.set(round.id()));
              buttons.add(btn);
            }
            standingsButtons.getChildren().setAll(buttons);
            if (rs.isEmpty()) {
              standingsRound.set(null);
              standings.getItems().clear();
              return;
            }
            Round.Id toSelect = rs.getLast().id();
            for (Round r : rs) {
              if (r.id().equals(previous)) toSelect = r.id();
            }
            if (toSelect.equals(standingsRound.get())) {
              loadStandingsFor.accept(toSelect);
            } else {
              standingsRound.set(toSelect);
            }
          }));
    };
    tournament.addListener(_ -> reloadStandings.run());

    Runnable reloadClosed = () -> {
      if (tournament.get() == null) return;
      backend
          .isTournamentClosed(tournament.get().id())
          .thenAccept(c -> Platform.runLater(() -> tournamentClosed.set(c)));
    };
    tournament.addListener(_ -> reloadClosed.run());

    rounds(currentRound, backend, reloadStandings, tournamentClosed);

    var status = new Text();
    var roundButton = new Button("Create next round");

    Runnable reloadRounds = () -> {
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
                                          roundLimitReached.set(
                                              tournament.get() != null
                                                  && rounds.size() >= tournament.get().rounds());
                                          if (rounds.isEmpty()) return;
                                          currentRound.set(rounds.getFirst().id());
                                      }));
    };

    tournament.addListener(_ -> reloadRounds.run());

    roundButton.setOnAction(_ -> {
                 if (tournament.get() == null) return;
                 backend.generateSwissRound(
                              tournament.get().id(),
                              tournament.get().start(),
                              tournament.get().end())
                      .thenAccept(err -> Platform.runLater(() -> {
                          if (err == null) reloadRounds.run();
                          else status.setText("Error: " + err);
                      }));
                });
    roundButton.disableProperty().bind(tournamentClosed.or(roundLimitReached));

    var closeButton = new Button("Zakończ turniej");
    closeButton.disableProperty().bind(tournamentClosed);
    closeButton.setOnAction(_ -> {
      if (tournament.get() == null) return;
      backend.closeTournament(tournament.get().id())
          .thenAccept(err -> Platform.runLater(() -> {
            if (err == null) {
              status.setText("Turniej zakończony");
              tournamentClosed.set(true);
              reloadRounds.run();
              reloadStandings.run();
            } else {
              status.setText("Error: " + err);
            }
          }));
    });

    getChildren().addAll(Util.inline(roundButton, closeButton), status);
    getChildren().addAll(new Label("Standings:"), standingsButtons, standings);
  }

  private void players(String text, String promptText, Supplier<CompletableFuture<List<PlayerBrief>>> players, Function<Player.Id, CompletableFuture<String>> onAdd, Backend backend) {
    var label = new Label(text);
    var list = new ListView<PlayerDataEntry>();
    var items = FXCollections.<PlayerDataEntry>observableArrayList();
    list.setItems(items);
    label.setLabelFor(list);

    Runnable reload = () -> {
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
    };

    tournament.addListener(_ -> reload.run());

    var input = new PlayerInput(backend);
    input.setMaxHeight(440);
    if (input.getChildren().get(0) instanceof HBox searchRow
        && searchRow.getChildren().get(1) instanceof TextField search
        && input.getChildren().get(1) instanceof ListView<?> dropdownList) {
      var showList =
          search.focusedProperty()
              .or(dropdownList.focusedProperty())
              .or(search.textProperty().isNotEmpty());
      dropdownList.visibleProperty().bind(showList);
      dropdownList.managedProperty().bind(showList);
    }
    var status = new Text();
    var addButton = new Button("Add");

    addButton.setOnAction(_ -> {
        var selectedPlayer = input.getValue();
        if (selectedPlayer == null) {
            status.setText(promptText);
            return;
        }

        if (tournament.get() == null) return;
        status.setText("Wait...");


        onAdd.apply(selectedPlayer)
                        .thenAccept(err->
                                Platform.runLater(() -> {
                                    if (err == null) {
                                        status.setText("");
                                        reload.run();
                                    } else {
                                        status.setText("Error: " + err);
                                    }
                                }));
    });
    getChildren().addAll(label, list, Util.inline(addButton, input, status));
  }

  private void rounds(ObservableObjectValue<Round.Id> currentRound, Backend backend, Runnable onResultsChanged, ObservableBooleanValue closed) {
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

    Runnable reloadGames = () -> {
      if (currentRound.get() == null) return;
      backend
          .getRoundGames(currentRound.get())
          .thenAccept(v -> Platform.runLater(() -> rounds.getItems().setAll(v)));
    };
    currentRound.addListener(_ -> reloadGames.run());

    var actionCol = new TableColumn<Game, Void>("Action");
    actionCol.setCellFactory(col -> new TableCell<>() {
      private final Button btn = new Button();
      {
        btn.disableProperty().bind(closed);
        btn.setOnAction(_ -> {
          int idx = getIndex();
          if (idx < 0 || idx >= getTableView().getItems().size()) return;
          Game game = getTableView().getItems().get(idx);
          Round.Id roundId = currentRound.get();
          if (roundId == null) return;
          openResultDialog(game, roundId, backend, () -> {
            reloadGames.run();
            onResultsChanged.run();
          });
        });
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
          setGraphic(null);
          return;
        }
        Game g = getTableView().getItems().get(getIndex());
        btn.setText(g.over() == null ? "Set result" : "Edit result");
        setGraphic(btn);
      }
    });
    actionCol.setMinWidth(110);
    rounds.getColumns().add(actionCol);

    getChildren().addAll(rounds);
  }

  private TableView<Standing> standingsTable() {
    var table = new TableView<Standing>();

    var place = new TableColumn<Standing, String>("#");
    place.setCellFactory(col -> new TableCell<>() {
      @Override
      protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        setText(empty || getIndex() < 0 ? null : Integer.toString(getIndex() + 1));
      }
    });
    place.setMaxWidth(40);
    table.getColumns().add(place);

    var player = new TableColumn<Standing, String>("Player");
    player.setCellValueFactory(
        p -> new SimpleStringProperty(
            p.getValue().player().name() + " " + p.getValue().player().surname()));
    player.setMinWidth(200);
    table.getColumns().add(player);

    var rating = new TableColumn<Standing, String>("Rating");
    rating.setCellValueFactory(
        p -> new SimpleStringProperty(Integer.toString(p.getValue().player().rating())));
    rating.setMaxWidth(80);
    table.getColumns().add(rating);

    var score = new TableColumn<Standing, String>("Score");
    score.setCellValueFactory(
        p -> new SimpleStringProperty(Float.toString(p.getValue().score())));
    score.setMaxWidth(80);
    table.getColumns().add(score);

    return table;
  }

  private static final List<String> RESULTS = List.of("1-0", "0-1", "1/2-1/2", "0-0", "+-", "-+");

  private static boolean reasonMatchesResult(GameOverReason r, String result) {
    if (r == null || result == null) return false;
    return switch (result) {
      case "1-0", "0-1", "+-", "-+" -> r.winScore() == 1f && r.loseScore() == 0f;
      case "1/2-1/2"                -> r.winScore() == 0.5f && r.loseScore() == 0.5f;
      case "0-0"                    -> r.winScore() == 0f && r.loseScore() == 0f;
      default -> false;
    };
  }

  private static String resultFromOver(GameOverReason r, boolean whiteWon) {
    if (r == null) return null;
    if (r.winScore() == 0.5f && r.loseScore() == 0.5f) return "1/2-1/2";
    if (r.winScore() == 0f   && r.loseScore() == 0f)   return "0-0";
    if (r.winScore() == 1f   && r.loseScore() == 0f)   return whiteWon ? "1-0" : "0-1";
    return null;
  }

  private static int[] ratingChangeFor(String result) {
    return switch (result) {
      case "1-0", "+-" -> new int[]{ +10, -10 };
      case "0-1", "-+" -> new int[]{ -10, +10 };
      default          -> new int[]{   0,   0 };
    };
  }

  private void openResultDialog(Game game, Round.Id roundId, Backend backend, Runnable onSaved) {
    var resultBox = new ComboBox<String>();
    resultBox.getItems().setAll(RESULTS);

    var reasonBox = new ComboBox<GameOverReason>();
    reasonBox.setConverter(new StringConverter<>() {
      @Override public String toString(GameOverReason v) { return v == null ? "" : v.description(); }
      @Override public GameOverReason fromString(String s) { return null; }
    });

    resultBox.valueProperty().addListener((_, _, result) -> {
      if (globals.get() == null) return;
      GameOverReason current = reasonBox.getValue();
      var filtered = globals.get().gameOverReasons().values().stream()
          .filter(r -> reasonMatchesResult(r, result))
          .toList();
      reasonBox.getItems().setAll(filtered);
      if (current != null && filtered.contains(current)) {
        reasonBox.setValue(current);
      } else if (filtered.size() == 1) {
        reasonBox.setValue(filtered.get(0));
      } else {
        reasonBox.setValue(null);
      }
    });

    var arbiterBox = new ComboBox<PlayerBrief>();
    arbiterBox.setConverter(new StringConverter<>() {
      @Override public String toString(PlayerBrief v) { return v == null ? "" : v.toString(); }
      @Override public PlayerBrief fromString(String s) { return null; }
    });

    backend.getTournamentArbiters(tournament.get().id())
        .thenAccept(list -> Platform.runLater(() -> {
          arbiterBox.getItems().setAll(list);
          if (game.over() != null) {
            for (PlayerBrief a : list) {
              if (a.id().equals(game.over().arbiter().id())) {
                arbiterBox.setValue(a);
                break;
              }
            }
          }
        }));

    if (game.over() != null) {
      GameOverReason existing = globals.get() == null
          ? null
          : globals.get().gameOverReason(game.over().reason());
      reasonBox.setValue(existing);
      resultBox.setValue(resultFromOver(existing, game.over().whiteWon()));
    }

    var status = new Text();
    var ok = new Button("Save");
    var cancel = new Button("Cancel");

    var box = new VBox(
        8,
        new Label("White: " + game.white()),
        new Label("Black: " + game.black()),
        new Label("Result"), resultBox,
        new Label("Reason"), reasonBox,
        new Label("Arbiter"), arbiterBox,
        Util.inline(ok, cancel),
        status);
    box.setPadding(new Insets(12));

    var stage = new Stage();
    stage.setTitle("Game result");
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(new Scene(box));

    cancel.setOnAction(_ -> stage.close());
    ok.setOnAction(_ -> {
      if (resultBox.getValue() == null) { status.setText("Wybierz wynik"); return; }
      if (reasonBox.getValue() == null) { status.setText("Wybierz powód"); return; }
      if (arbiterBox.getValue() == null) { status.setText("Wybierz sędziego"); return; }

      String result = resultBox.getValue();
      boolean whiteWon = !result.equals("0-1") && !result.equals("-+");
      int[] ratingChange = ratingChangeFor(result);
      int whiteChange = ratingChange[0];
      int blackChange = ratingChange[1];
      ok.setDisable(true);
      backend.setGameResult(
              roundId, game.white().id(), whiteWon,
              reasonBox.getValue().id(), arbiterBox.getValue().id(),
              whiteChange, blackChange)
          .thenAccept(err -> Platform.runLater(() -> {
            if (err == null) {
              stage.close();
              onSaved.run();
            } else {
              ok.setDisable(false);
              status.setText("Error: " + err);
            }
          }));
    });

    stage.show();
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
