package org.tcs.ui.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

public class SimpleList<T> extends VBox {
  private Consumer<T> onSelected;
  private Runnable onCreate = () -> {};
  private final ObservableList<SimpleListEntry<T>> items = FXCollections.observableArrayList();
  private final Consumer<Consumer<List<? extends SimpleListEntry<T>>>> loadingFunc;

  public SimpleList(Consumer<Consumer<List<? extends SimpleListEntry<T>>>> loadingFunc) {
    this.loadingFunc = loadingFunc;
    var create = new Button("Create");
    create.setOnAction(_ -> onCreate.run());
    getChildren().add(create);

    var list = new ListView<SimpleListEntry<T>>();

    list.setItems(items);
    getChildren().add(list);

    list.setOnMouseClicked(
      _ -> {
        if (onSelected == null) return;

        SimpleListEntry<T> item = list.getSelectionModel().getSelectedItem();
        list.getSelectionModel().clearSelection();
        if (item == null) return;

        onSelected.accept(item.id());
      });
  }

  public void load() {
    loadingFunc.accept(items::setAll);
  }

  public void setOnSelected(Consumer<T> onSelected) {
    this.onSelected = onSelected;
  }

  public void setOnCreate(Runnable onCreate) {
    this.onCreate = onCreate;
  }
}
