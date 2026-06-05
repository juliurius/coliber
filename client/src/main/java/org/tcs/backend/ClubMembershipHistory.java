package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;

public record ClubMembershipHistory(
    @NotNull ClubBrief club,
    @NotNull PlayerBrief player,
    @NotNull Date since,
    @Nullable Date until) {}
