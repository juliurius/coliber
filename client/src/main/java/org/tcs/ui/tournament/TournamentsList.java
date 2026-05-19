package org.tcs.ui.tournament;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;

import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;
import org.tcs.backend.Tournament;

import java.util.function.Consumer;

public class TournamentsList extends VBox {
  private Consumer<Tournament.Id> onSelected;

  public TournamentsList(Backend backend) {
    var list = new ListView<ListEntry>();

    var items = FXCollections.<ListEntry>observableArrayList();
    list.setItems(items);
    getChildren().add(list);

    list.setOnMouseClicked(
        _ -> {
          if (onSelected == null) return;

          ListEntry item = list.getSelectionModel().getSelectedItem();
          if (item == null) return;

          onSelected.accept(item.id);
        });

    backend
        .getTournaments()
        .thenAccept(
            tournaments ->
                Platform.runLater(
                    () ->
                        items.setAll(
                            tournaments.stream().map(ListEntry::new).toArray(ListEntry[]::new))));
  }

  public void setOnSelected(Consumer<Tournament.Id> onSelected) {
    this.onSelected = onSelected;
  }
}
