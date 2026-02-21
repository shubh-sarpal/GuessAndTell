package com.practice.guessandtell.dto;

import java.util.UUID;

public record GameResponse(
        UUID gameId,
        String code,
        String status
) {
}
