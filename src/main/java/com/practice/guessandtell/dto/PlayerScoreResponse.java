package com.practice.guessandtell.dto;

public record PlayerScoreResponse(
        String username,
        int totalScore
) {
}
