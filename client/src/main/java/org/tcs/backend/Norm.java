package org.tcs.backend;

import org.jetbrains.annotations.NotNull;

public record Norm(@NotNull Tournament.Id tournament, @NotNull Title.Id title) {}
