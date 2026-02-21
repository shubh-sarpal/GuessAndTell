package com.practice.guessandtell.dto;

import java.util.List;

public record GameStateResponse(
        String status,
        int currentRound,
        int totalRounds,
        String imageUrl,
        List<PlayerScoreResponse> leaderboard
) {
}
