package org.tcs.ui.club;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;
import org.tcs.ui.player.PlayerDataEntry;

import java.util.function.Consumer;

public class ClubDetails extends VBox {
  private final SimpleObjectProperty<Club> club = new SimpleObjectProperty<>();
  private final ObjectProperty<Globals> globals = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public ClubDetails(Backend backend) {
    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Club(null)));

    var nameLabel = new Label();
    nameLabel.textProperty().bind(club.map(v -> "Name: " + v.name()));
    getChildren().add(nameLabel);

    var cityLabel = new Label();
    cityLabel
        .textProperty()
        .bind(
            Bindings.createStringBinding(
                () -> {
                  if (globals.get() == null || club.get().city() == null) return "City: None";
                  return "City: " + globals.get().city(club.get().city()).name();
                },
                club,
                globals));
    getChildren().add(cityLabel);

    var presidentLabel = new Label("President: ");
    var presidentLink = new Hyperlink();
    presidentLink.setOnAction(
        _ -> {
          if (club.get().president() == null) return;
          onNav.get().accept(new Nav.Player.Details(club.get().president().id()));
        });
    presidentLink
        .textProperty()
        .bind(club.map(v -> v.president() == null ? "None" : v.president().toString()));
    presidentLink.disableProperty().bind(club.map(v -> v.president() == null));
    presidentLabel.setLabelFor(presidentLink);
    getChildren().add(Util.inline(presidentLabel, presidentLink));

    var clubPlayersLabel = new Label("Club Players: ");
    var clubPlayersList = new ListView<PlayerDataEntry>();
    var items = FXCollections.<PlayerDataEntry>observableArrayList();
    clubPlayersList.setItems(items);
    clubPlayersLabel.setLabelFor(clubPlayersList);
    club.addListener(
        _ -> {
          if (club.get() == null) return;
          backend
              .getClubMembers(club.get().id())
              .thenAccept(
                  members -> Platform.runLater(
                      () -> items.setAll(members.stream().map(brief -> {
                        var entry = new PlayerDataEntry(brief);
                        entry.onNavProperty().bind(onNav);
                        return entry;
                      }).toList())));
        });
    getChildren().addAll(clubPlayersLabel, clubPlayersList);
  }

  public ObjectProperty<Club> clubProperty() {
    return club;
  }

  public ObjectProperty<Globals> globalsProperty() {
    return globals;
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }
}
