package org.tcs.ui.system;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import org.tcs.backend.Backend;
import org.tcs.backend.TournamentSystem;
import org.tcs.ui.Nav;
import org.tcs.ui.util.SimpleList;

import java.util.function.Consumer;

public class Systems extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Nav.TournamentSystem> nav =
    new SimpleObjectProperty<>(new Nav.TournamentSystem.All());

  public Systems(Backend backend) {
    var list = new SimpleList<TournamentSystem>(f -> backend
      .getTournamentSystems()
      .thenAccept(
        systems ->
          Platform.runLater(
            () -> f.accept(
              systems.stream().map(ListEntry::new).toList()))));
    var creator = new Creator(backend);

    creator.setOnBack(() -> onNav.get().accept(new Nav.TournamentSystem.All()));

    setCenter(list);
    centerProperty().bind(nav.map(nav -> {
      if (nav instanceof Nav.TournamentSystem.All) {
        list.load();
        return list;
      }

      return creator;
    }));

    list.setOnCreate(() -> onNav.get().accept(new Nav.TournamentSystem.Create()));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Nav.TournamentSystem> navProperty() {
    return nav;
  }
}
