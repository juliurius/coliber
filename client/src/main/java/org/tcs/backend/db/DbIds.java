package org.tcs.backend.db;

import org.tcs.backend.ArbiterClass;
import org.tcs.backend.City;
import org.tcs.backend.Club;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerClass;
import org.tcs.backend.Title;
import org.tcs.backend.Tournament;

public final class DbIds {
  private DbIds() {}

  public record CityId(int value) implements City.Id {}

  public record PlayerId(int value) implements Player.Id {}

  public record ClubId(int value) implements Club.Id {}

  public record TournamentId(int value) implements Tournament.Id {}

  public record PlayerClassId(int value) implements PlayerClass.Id {}

  public record ArbiterClassId(int value) implements ArbiterClass.Id {}

  public record TitleId(int value) implements Title.Id {}
}
