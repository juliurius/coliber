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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.tcs.Globals;
import org.tcs.backend.Backend;
import org.tcs.backend.Club;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerBrief;
import org.tcs.ui.Nav;
import org.tcs.ui.Util;

import java.util.function.Consumer;

public class ClubDetails extends VBox {
  private final SimpleObjectProperty<Club> club = new SimpleObjectProperty<>();
  private final ObjectProperty<Globals> globals = new SimpleObjectProperty<>();

  private final ObjectProperty<Consumer<Nav>> onNav = new SimpleObjectProperty<>(_ -> {});

  public ClubDetails(Backend backend, Club.Id clubId) {
    backend.getClub(clubId).thenAccept(club -> Platform.runLater(() -> this.club.set(club)));

    var button = new Button("Back");
    getChildren().add(button);
    button.setOnAction(_ -> onNav.get().accept(new Nav.Club.All()));

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
    var setPresident = new Button("Set President");
    setPresident.setOnAction(_ -> onNav.get().accept(new Nav.Club.SetPresident(club.get().id())));
    getChildren().add(Util.inline(presidentLabel, presidentLink, setPresident));

    var playersLabel = new Label("Club Members: ");
    var addPlayer = new Button("Add");
    addPlayer.setOnAction(_ -> onNav.get().accept(new Nav.Club.AddMember(club.get().id())));
    var playerList = new ListView<MemberEntry>();
    var items = FXCollections.<MemberEntry>observableArrayList();
    playerList.setItems(items);
    playersLabel.setLabelFor(playerList);
    club.addListener(
        _ -> {
          if (club.get() == null) return;
          backend
              .getClubMembers(club.get().id())
              .thenAccept(
                  members ->
                      Platform.runLater(
                          () ->
                              items.setAll(
                                  members.stream()
                                      .map(
                                          brief -> {
                                            var entry = new MemberEntry(brief);
                                            entry.onNav = ev -> onNav.get().accept(ev);
                                            entry.onDelete = v -> {
                                              items.remove(v);
                                              backend.removeClubMember(v.id);
                                            };
                                            return entry;
                                          })
                                      .toList())));
        });
    getChildren().addAll(Util.inline(playersLabel, addPlayer), playerList);
  }

  public ObjectProperty<Globals> globalsProperty() {
    return globals;
  }

  public ObjectProperty<Consumer<Nav>> onNavProperty() {
    return onNav;
  }

  private static class MemberEntry extends HBox {
    Consumer<Nav> onNav = _ -> {};
    Consumer<MemberEntry> onDelete = _ -> {};
    final Player.Id id;

    MemberEntry(PlayerBrief entry) {
      id = entry.id();
      var link = new Hyperlink(entry.toString());
      link.setPrefWidth(300);
      link.setOnAction(_ -> onNav.accept(new Nav.Player.Details(entry.id())));
      var del = new Button("Exile");
      del.setOnAction(_ -> onDelete.accept(this));
      getChildren().addAll(link, del);
    }
  }
}
