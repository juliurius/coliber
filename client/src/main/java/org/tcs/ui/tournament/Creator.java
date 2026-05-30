package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.City;
import org.tcs.backend.PlayerBrief;
import org.tcs.backend.Tempo;
import org.tcs.backend.TournamentSystem;
import org.tcs.ui.Util;
import org.tcs.ui.util.FormButtons;
import org.tcs.ui.util.StringInput;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Creator extends VBox {
  private Runnable onBack = () -> {};

  public Creator(Backend backend, CompletableFuture<Globals> globals) {
    var name = new StringInput("Name");
    var start = new StringInput("Start (yyyy-mm-dd hh:mm:ss)");
    var end = new StringInput("End (yyyy-mm-dd hh:mm:ss)");
    var address = new StringInput("Address");

    var city = new ComboBox<City>();
    setLabel(city, City::name);
    globals.thenAccept(
        g -> Platform.runLater(() -> city.getItems().setAll(g.cities().values())));

    var tempo = new ComboBox<Tempo>();
    setLabel(tempo, Tempo::name);
    backend.getTempos().thenAccept(v -> Platform.runLater(() -> tempo.getItems().setAll(v)));

    var system = new ComboBox<TournamentSystem>();
    setLabel(system, TournamentSystem::name);
    backend
        .getTournamentSystems()
        .thenAccept(v -> Platform.runLater(() -> system.getItems().setAll(v)));

    var organiser = new ComboBox<PlayerBrief>();
    backend.getPlayers().thenAccept(v -> Platform.runLater(() -> organiser.getItems().setAll(v)));

    var mainArbiter = new ComboBox<PlayerBrief>();
    backend.getArbiters().thenAccept(v -> Platform.runLater(() -> mainArbiter.getItems().setAll(v)));

    var status = new Text();
    var buttons =
        new FormButtons(
            () -> {
              Timestamp startTs;
              Timestamp endTs;
              try {
                startTs = Timestamp.valueOf(normalizeDate(start.getValue()));
                endTs = Timestamp.valueOf(normalizeDate(end.getValue()));
              } catch (IllegalArgumentException e) {
                status.setText("Error: invalid date, use yyyy-mm-dd hh:mm:ss");
                return;
              }

              var organiserBrief = organiser.getValue();
              var arbiterBrief = mainArbiter.getValue();
              var tempoValue = tempo.getValue();
              var systemValue = system.getValue();

              if (name.getValue().isBlank()
                  || tempoValue == null
                  || systemValue == null
                  || organiserBrief == null
                  || arbiterBrief == null) {
                status.setText(
                    "Error: name, tempo, system, organiser and main arbiter are required");
                return;
              }

              status.setText("Wait...");
              backend
                  .createTournament(
                      name.getValue(),
                      startTs,
                      endTs,
                      city.getValue() == null ? null : city.getValue().id(),
                      address.getValue(),
                      tempoValue,
                      systemValue,
                      organiserBrief.id(),
                      arbiterBrief.id())
                  .whenComplete(
                      (id, err) ->
                          Platform.runLater(
                              () -> {
                                if (err == null) {
                                  status.setText("");
                                  onBack.run();
                                } else {
                                  status.setText("Error: " + rootMessage(err));
                                }
                              }));
            },
            () -> onBack.run());

    getChildren()
        .addAll(
            name,
            start,
            end,
            address,
            Util.inline(new Label("City: "), city),
            Util.inline(new Label("Tempo: "), tempo),
            Util.inline(new Label("System: "), system),
            Util.inline(new Label("Organiser: "), organiser),
            Util.inline(new Label("Main arbiter: "), mainArbiter),
            buttons,
            status);
  }

  public void setOnBack(Runnable onBack) {
    this.onBack = onBack;
  }

  private static <T> void setLabel(ComboBox<T> box, Function<T, String> label) {
    box.setConverter(
        new StringConverter<>() {
          @Override
          public String toString(T value) {
            return value == null ? "" : label.apply(value);
          }

          @Override
          public T fromString(String string) {
            return null;
          }
        });
  }

  private static String normalizeDate(String value) {
    return value.trim().replaceAll("\\s+", " ");
  }

  private static String rootMessage(Throwable error) {
    var cause = error;
    while (cause.getCause() != null) {
      cause = cause.getCause();
    }
    return cause.getMessage();
  }
}
