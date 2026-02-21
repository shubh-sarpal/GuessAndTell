package com.practice.guessandtell.dto;

import java.util.UUID;

public record SubmitGuessRequest(
        UUID playerId,
        double latitude,
        double longitude,
        int year
) {}
