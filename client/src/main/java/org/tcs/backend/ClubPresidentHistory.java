package org.tcs.backend;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Date;

public record ClubPresidentHistory(
    @NotNull ClubBrief club,
    @NotNull PlayerBrief president,
    @NotNull Date since,
    @Nullable Date until) {}
