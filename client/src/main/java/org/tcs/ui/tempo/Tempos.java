package org.tcs.ui.tempo;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import org.tcs.backend.Backend;
import org.tcs.backend.Tempo;
import org.tcs.ui.Nav;
import org.tcs.ui.util.SimpleList;

import java.util.function.Consumer;

public class Tempos extends BorderPane {
  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});
  private final ObjectProperty<Nav.Tempo> nav =
    new SimpleObjectProperty<>(new Nav.Tempo.All());

  public Tempos(Backend backend) {
    var list = new SimpleList<Tempo>(f -> backend
      .getTempos()
      .thenAccept(
        tempos ->
          Platform.runLater(
            () -> f.accept(
              tempos.stream().map(ListEntry::new).toList()))));
    var creator = new Creator(backend);

    creator.setOnBack(() -> onNav.get().accept(new Nav.Tempo.All()));

    setCenter(list);
    centerProperty().bind(nav.map(nav -> {
      if (nav instanceof Nav.Tempo.All) {
        list.load();
        return list;
      }

      return creator;
    }));

    list.setOnCreate(() -> onNav.get().accept(new Nav.Tempo.Create()));
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  public ObjectProperty<Nav.Tempo> navProperty() {
    return nav;
  }
}
