package org.tcs;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
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
    var backend = new Mock();

    var cities = backend.getCities();
    var tempos = backend.getTempos();
    var systems = backend.getTournamentSystems();
    CompletableFuture<Globals> globals =
        CompletableFuture.allOf(cities, tempos, systems)
            .thenApply(_ -> new Globals(cities.join(), tempos.join(), systems.join()));

    primaryStage.setTitle("Chess Manager");

    var pane = new TabPane();

    var tournaments = new Tournaments(backend, globals);
    var players = new Players(backend);
    pane.getTabs().add(new Tab("Tournaments", tournaments));
    pane.getTabs().add(new Tab("Players", players));
    pane.getTabs().add(new Tab("Clubs", new Clubs(backend, globals)));
    pane.getTabs().add(new Tab("Tempos", new Tempos(backend)));
    pane.getTabs().add(new Tab("Systems", new Systems(backend)));

    Consumer<Nav> onNav = e -> {
      System.out.println("Nav: " + e);
      if (e instanceof Nav.Tournament(org.tcs.backend.Tournament.Id id)) {
        pane.getSelectionModel().select(0);
        tournaments.tournamentProperty().set(id);
      } else if (e instanceof Nav.Player(org.tcs.backend.Player.Id id)) {
        pane.getSelectionModel().select(1);
        players.playerProperty().set(id);
      }
    };

    tournaments.onNavProperty().set(onNav);
    players.onNavProperty().set(onNav);

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
