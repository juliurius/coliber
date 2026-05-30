package org.tcs;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.tcs.backend.Backend;
import org.tcs.backend.db.DbBackend;
import org.tcs.backend.db.DbConfig;
import org.tcs.backend.mock.Mock;
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
    Backend backend;
    if (System.getenv("MOCK") == null) {
      backend = new DbBackend(DbConfig.fromEnv());
    } else {
      backend = new Mock();
    }

    var globals = makeGlobals(backend);

    primaryStage.setTitle("Chess Manager");

    var pane = new TabPane();
    pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

    var tournaments = new Tournaments(backend, globals);
    var players = new Players(backend, globals);
    var clubs = new Clubs(backend, globals);
    var tempos = new Tempos(backend);
    var systems = new Systems(backend);
    pane.getTabs().add(new Tab("Tournaments", tournaments));
    pane.getTabs().add(new Tab("Players", players));
    pane.getTabs().add(new Tab("Clubs", clubs));
    pane.getTabs().add(new Tab("Tempos", tempos));
    pane.getTabs().add(new Tab("Systems", systems));

    Consumer<Nav> onNav =
        e -> {
          if (e instanceof Nav.Tournament n) {
            pane.getSelectionModel().select(0);
            tournaments.navProperty().set(n);
          } else if (e instanceof Nav.Player n) {
            pane.getSelectionModel().select(1);
            players.navProperty().set(n);
          } else if (e instanceof Nav.Club n) {
            pane.getSelectionModel().select(2);
            clubs.navProperty().set(n);
          } else if (e instanceof Nav.Tempo t) {
            pane.getSelectionModel().select(3);
            tempos.navProperty().set(t);
          } else if (e instanceof Nav.TournamentSystem t) {
            pane.getSelectionModel().select(4);
            systems.navProperty().set(t);
          }
        };

    tournaments.onNavProperty().set(onNav);
    players.onNavProperty().set(onNav);
    clubs.onNavProperty().set(onNav);
    tempos.onNavProperty().set(onNav);
    systems.onNavProperty().set(onNav);

    onNav.accept(new Nav.Tournament.All());

    Scene scene = new Scene(pane, 1000, 800);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private CompletableFuture<Globals> makeGlobals(Backend backend) {
    var cities = backend.getCities();
    var playerClasses = backend.getPlayerClasses();
    var arbiterClasses = backend.getArbiterClasses();
    var titles = backend.getTitles();
    var gameOverReasons = backend.getGameOverReasons();
    return       CompletableFuture.allOf(
          cities, playerClasses, arbiterClasses, titles, gameOverReasons)
        .thenApply(
          _ ->
            new Globals(
              cities.join(),
              playerClasses.join(),
              arbiterClasses.join(),
              titles.join(),
              gameOverReasons.join()));
  }

  // IDEA marks this as unused, even though it's used.
  @SuppressWarnings("unused")
  static void main() {
    Application.launch();
  }
}
