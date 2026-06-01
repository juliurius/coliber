package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record RankingEntry(int position, @NotNull PlayerBrief player) {}
