package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record ClubBrief(@NotNull Club.Id id, @NotNull String name, City.Id city) {}
