package org.tcs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class Main extends Application {
  @Override
  public void start(@NotNull Stage primaryStage) {
    primaryStage.setTitle("Chess Manager");

    var tabs = List.of("Tournaments", "Players", "Clubs", "Arbiters");

    var pane = new TabPane();

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
