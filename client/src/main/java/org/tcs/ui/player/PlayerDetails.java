package org.tcs.ui.player;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.ClubBrief;
import org.tcs.backend.Norm;
import org.tcs.backend.Penalty;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerStats;
import org.tcs.backend.TournamentBrief;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;

import java.sql.Date;
import java.util.function.Consumer;

public class PlayerDetails extends VBox {
  private final SimpleObjectProperty<Player> player = new SimpleObjectProperty<>();
  private final SimpleObjectProperty<PlayerStats> stats = new SimpleObjectProperty<>();
  private final ObjectProperty<Globals> globals = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public PlayerDetails(Backend backend, Player.Id id) {
    backend.getPlayer(id).thenAccept(player -> Platform.runLater(() -> this.player.set(player)));
    backend.getPlayerStats(id).thenAccept(stats -> Platform.runLater(() -> this.stats.set(stats)));

    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Player.All()));

    var idLabel = new Label();
    idLabel.textProperty().bind(player.map(v -> "ID: " + v.id()));
    getChildren().add(idLabel);

    var nameLabel = new Label();
    nameLabel.textProperty().bind(player.map(v -> "Name: " + v.name()));
    getChildren().add(nameLabel);

    var surnameLabel = new Label();
    surnameLabel.textProperty().bind(player.map(v -> "Surname: " + v.surname()));
    getChildren().add(surnameLabel);

    var classicalRatingLabel = new Label();
    classicalRatingLabel.textProperty().bind(player.map(v -> "Rating (classical): " + v.ratingClassical()));
    getChildren().add(classicalRatingLabel);

    var rapidRatingLabel = new Label();
    rapidRatingLabel.textProperty().bind(player.map(v -> "Rating (rapid): " + v.ratingRapid()));
    getChildren().add(rapidRatingLabel);

    var blitzRatingLabel = new Label();
    blitzRatingLabel.textProperty().bind(player.map(v -> "Rating (blitz): " + v.ratingBlitz()));
    getChildren().add(blitzRatingLabel);

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
//    var changePlayerClass = new Button("Change");
//    changePlayerClass.setOnAction(_ -> onNav.get().accept(new Nav.Player.SetPlayerClass(player.get().id())));
//    getChildren().add(Util.inline(playerClassLabel, changePlayerClass));
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
    var changeArbiterClass = new Button("Change");
    changeArbiterClass.setOnAction(_ -> onNav.get().accept(new Nav.Player.SetArbiterClass(player.get().id())));
    getChildren().add(Util.inline(arbiterClassLabel, changeArbiterClass));

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
//    var changeTitle = new Button("Change");
//    changeTitle.setOnAction(_ -> onNav.get().accept(new Nav.Player.SetTitle(player.get().id())));
//    getChildren().add(Util.inline(titleLabel, changeTitle));
    getChildren().add(titleLabel);

    var clubLabel = new Label("Club: ");
    var clubLink = new Hyperlink();
    clubLink.setOnAction(
        _ -> {
          if (player.get().club() == null) return;
          onNav.get().accept(new Nav.Club.Details(player.get().club().id()));
        });
    clubLink.textProperty().bind(player.map(v -> v.club() == null ? "None" : v.club().name()));
    clubLink.disableProperty().bind(player.map(v -> v.club() == null));
    clubLabel.setLabelFor(clubLink);
    getChildren().add(Util.inline(clubLabel, clubLink));

    var membershipHistoryLabel = new Label("Club Membership History");
    var membershipHistory = new ListView<ClubHistoryEntry>();
    backend.getPlayerClubMembershipHistory(id).thenAccept(history -> Platform.runLater(() -> {
      membershipHistory.getItems().clear();
      for (var entry : history) {
        membershipHistory.getItems().add(new ClubHistoryEntry(
            entry.club(), entry.since(), entry.until(), ev -> onNav.get().accept(ev)));
      }
    }));
    getChildren().addAll(membershipHistoryLabel, membershipHistory);

    var presidentHistoryLabel = new Label("Club President History");
    var presidentHistory = new ListView<ClubHistoryEntry>();
    backend.getPlayerClubPresidentHistory(id).thenAccept(history -> Platform.runLater(() -> {
      presidentHistory.getItems().clear();
      for (var entry : history) {
        presidentHistory.getItems().add(new ClubHistoryEntry(
            entry.club(), entry.since(), entry.until(), ev -> onNav.get().accept(ev)));
      }
    }));
    getChildren().addAll(presidentHistoryLabel, presidentHistory);

    var tournamentsLabel = new Label();
    tournamentsLabel
        .textProperty()
        .bind(stats.map(v -> "Tournaments (" + v.tournamentsPlayed() + ")"));
    var tournaments = new TableView<TournamentBrief>();

    var tournamentNameColumn = new TableColumn<TournamentBrief, String>("Tournament");
    tournamentNameColumn.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().name()));
    tournamentNameColumn.setMinWidth(300);
    tournaments.getColumns().add(tournamentNameColumn);

    var tournamentStartColumn = new TableColumn<TournamentBrief, String>("Start");
    tournamentStartColumn.setCellValueFactory(
        p ->
            new SimpleStringProperty(
                p.getValue().start().toLocalDateTime().toLocalDate().toString()));
    tournamentStartColumn.setMinWidth(100);
    tournaments.getColumns().add(tournamentStartColumn);

    var tournamentEndColumn = new TableColumn<TournamentBrief, String>("End");
    tournamentEndColumn.setCellValueFactory(
        p ->
            new SimpleStringProperty(
                p.getValue().end().toLocalDateTime().toLocalDate().toString()));
    tournamentEndColumn.setMinWidth(100);
    tournaments.getColumns().add(tournamentEndColumn);
    VBox.setVgrow(tournaments, Priority.ALWAYS);

    backend
        .getPlayerTournaments(id)
        .thenAccept(v -> Platform.runLater(() -> tournaments.getItems().setAll(v)));

    tournaments.setOnMouseClicked(
        _ -> {
          var tournament = tournaments.getSelectionModel().getSelectedItem();
          if (tournament == null) return;
          onNav.get().accept(new Nav.Tournament.Details(tournament.id()));
        });

    getChildren().addAll(tournamentsLabel, tournaments);

    var penaltiesLabel = new Label();
    penaltiesLabel
        .textProperty()
        .bind(stats.map(v -> "Penalties (" + v.activePenalties() + " active)"));
    var addPenalty = new Button("Add");
    addPenalty.setOnAction(_ -> onNav.get().accept(new Nav.Player.AddPenalty(player.get().id())));
    var penalties = new TableView<Penalty>();

    var sinceColumn = new TableColumn<Penalty, String>("Since");
    sinceColumn.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().since().toLocalDate().toString()));
    sinceColumn.setMinWidth(100);
    penalties.getColumns().add(sinceColumn);

    var untilColumn = new TableColumn<Penalty, String>("Until");
    untilColumn.setCellValueFactory(
        p -> new SimpleStringProperty(
            p.getValue().until() == null ? "Lifetime" : p.getValue().until().toLocalDate().toString()));
    untilColumn.setMinWidth(100);
    penalties.getColumns().add(untilColumn);

    var roleColumn = new TableColumn<Penalty, String>("Role");
    roleColumn.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().roleContext().name()));
    roleColumn.setMinWidth(140);
    penalties.getColumns().add(roleColumn);

    var reasonColumn = new TableColumn<Penalty, String>("Reason");
    reasonColumn.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().reason()));
    reasonColumn.setMinWidth(200);
    penalties.getColumns().add(reasonColumn);

    var arbiterColumn = new TableColumn<Penalty, String>("Arbiter");
    arbiterColumn.setCellValueFactory(
        p -> new SimpleStringProperty(p.getValue().arbiter().toString()));
    arbiterColumn.setMinWidth(280);
    penalties.getColumns().add(arbiterColumn);
    VBox.setVgrow(penalties, Priority.ALWAYS);

    player.addListener(
        _ ->
            backend
                .getPlayerPenalties(player.get().id())
                .thenAccept(v -> Platform.runLater(() -> penalties.getItems().setAll(v))));

    getChildren().addAll(Util.inline(penaltiesLabel, addPenalty), penalties);

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
    VBox.setVgrow(norms, Priority.ALWAYS);

    player.addListener(
      _ ->
        backend
          .getPlayerNorms(player.get().id())
          .thenAccept(v -> Platform.runLater(() -> norms.getItems().setAll(v))));

    getChildren().addAll(normsLabel, norms);
  }

  public ObjectProperty<Globals> globalsProperty() {
    return globals;
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  private static class ClubHistoryEntry extends HBox {
    ClubHistoryEntry(ClubBrief club, Date since, Date until, Consumer<Nav> onNav) {
      var link = new Hyperlink(club.name());
      link.setPrefWidth(300);
      link.setOnAction(_ -> onNav.accept(new Nav.Club.Details(club.id())));
      getChildren().addAll(link, new Label(period(since, until)));
    }
  }

  private static String period(Date since, Date until) {
    return since.toLocalDate() + " - " + (until == null ? "now" : until.toLocalDate());
  }
}
