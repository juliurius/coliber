package org.tcs.ui.club;

import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;

public class ClubsList extends VBox {
  private Consumer<Club.Id> onSelected;

  public ClubsList(Backend backend) {
    var list = new ListView<ListEntry>();

    var items = FXCollections.<ListEntry>observableArrayList();
    list.setItems(items);
    getChildren().add(list);

    list.setOnMouseClicked(
      _ -> {
        if (onSelected == null) return;

        ListEntry item = list.getSelectionModel().getSelectedItem();
        list.getSelectionModel().clearSelection();
        if (item == null) return;

        onSelected.accept(item.id);
      });

    backend
      .getClubs()
      .thenAccept(
        clubs ->
          Platform.runLater(
            () -> items.setAll(
              clubs.stream().map(ListEntry::new).toArray(ListEntry[]::new))));
  }

  public void setOnSelected(Consumer<Club.Id> onSelected) {
    this.onSelected = onSelected;
  }}
