package com.practice.guessandtell.dto;

import jakarta.validation.constraints.NotBlank;

public record JoinGameRequest(
        @NotBlank String username
) {
}
