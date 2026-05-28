package org.tcs;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.tcs.backend.db.DbBackend;
import org.tcs.backend.db.DbConfig;
import org.tcs.ui.Nav;
import org.tcs.ui.club.Clubs;
import org.tcs.ui.player.Players;
import org.tcs.ui.system.Systems;
import org.tcs.ui.tempo.Tempos;
import org.tcs.ui.tournament.Tournaments;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Main extends Application {
  @Override
  public void start(@NotNull Stage primaryStage) {
    var backend = new DbBackend(DbConfig.fromEnv());

    var cities = backend.getCities();
    var tempos = backend.getTempos();
    var systems = backend.getTournamentSystems();
    var playerClasses = backend.getPlayerClasses();
    var arbiterClasses = backend.getArbiterClasses();
    var titles = backend.getTitles();
    var gameOverReasons = backend.getGameOverReasons();
    CompletableFuture<Globals> globals =
        CompletableFuture.allOf(
                cities, tempos, systems, playerClasses, arbiterClasses, titles, gameOverReasons)
            .thenApply(
                _ ->
                    new Globals(
                        cities.join(),
                        tempos.join(),
                        systems.join(),
                        playerClasses.join(),
                        arbiterClasses.join(),
                        titles.join(),
                        gameOverReasons.join()));

    primaryStage.setTitle("Chess Manager");

    var pane = new TabPane();
    pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

    var tournaments = new Tournaments(backend, globals);
    var players = new Players(backend, globals);
    var clubs = new Clubs(backend, globals);
    pane.getTabs().add(new Tab("Tournaments", tournaments));
    pane.getTabs().add(new Tab("Players", players));
    pane.getTabs().add(new Tab("Clubs", clubs));
    pane.getTabs().add(new Tab("Tempos", new Tempos(backend)));
    pane.getTabs().add(new Tab("Systems", new Systems(backend)));

    Consumer<Nav> onNav =
        e -> {
          if (e instanceof Nav.Tournament(org.tcs.backend.Tournament.Id id)) {
            pane.getSelectionModel().select(0);
            tournaments.tournamentProperty().set(id);
          } else if (e instanceof Nav.Player(org.tcs.backend.Player.Id id)) {
            pane.getSelectionModel().select(1);
            players.playerProperty().set(id);
          } else if (e instanceof Nav.Club(org.tcs.backend.Club.Id id)) {
            pane.getSelectionModel().select(2);
            clubs.clubProperty().set(id);
          }
        };

    tournaments.onNavProperty().set(onNav);
    players.onNavProperty().set(onNav);
    clubs.onNavProperty().set(onNav);

    onNav.accept(new Nav.Tournament(null));

    Scene scene = new Scene(pane, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  // IDEA marks this as unused, even though it's used.
  @SuppressWarnings("unused")
  static void main() {
    Application.launch();
  }
}
