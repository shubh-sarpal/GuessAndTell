package com.practice.guessandtell.dto;

import java.util.UUID;

public record JoinGameResponse(
        UUID playerId
) {
}
