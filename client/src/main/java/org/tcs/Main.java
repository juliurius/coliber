package org.tcs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.stage.Stage;

public class Main extends Application {
  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("Chess Manager");

    var root = new Label("Hello, World!");

    Scene scene = new Scene(root, 800, 600);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  static void main() {
    Application.launch();
  }
}
