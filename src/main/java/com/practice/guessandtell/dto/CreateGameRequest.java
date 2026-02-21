package com.practice.guessandtell.dto;

import com.practice.guessandtell.enums.GameMode;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequest(
        @NotNull GameMode mode,
        @Min(2) @Max(10) int maxPlayers
        ) {}
