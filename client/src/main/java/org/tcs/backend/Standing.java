package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record Standing(@NotNull PlayerBrief player, float score, int ratingChange) {}
