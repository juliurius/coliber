package org.tcs.backend.db;

import org.tcs.backend.ArbiterClass;
import org.tcs.backend.City;
import org.tcs.backend.Club;
import org.tcs.backend.GameOverReason;
import org.tcs.backend.Penalty;
import org.tcs.backend.PenaltyRoleContext;
import org.tcs.backend.Player;
import org.tcs.backend.PlayerClass;
import org.tcs.backend.Round;
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

  public record GameOverReasonId(int value) implements GameOverReason.Id {}

  public record PenaltyRoleContextId(int value) implements PenaltyRoleContext.Id {}

  public record PenaltyId(int value) implements Penalty.Id {}

  public record RoundId(int value) implements Round.Id {}
}
