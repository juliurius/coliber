package org.tcs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.tcs.backend.mock.Mock;
import org.tcs.ui.tournament.Tournaments;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    var tabs = List.of("Players", "Clubs", "Arbiters");

    var pane = new TabPane();

    pane.getTabs().add(new Tab("Tournaments", new Tournaments(backend, globals)));

    for (var tab : tabs) {
      pane.getTabs().add(new Tab(tab, new Label(tab)));
    }

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
