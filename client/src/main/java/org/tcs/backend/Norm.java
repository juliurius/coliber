package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record Norm(@NotNull TournamentBrief tournament, @NotNull Title.Id title) {}
