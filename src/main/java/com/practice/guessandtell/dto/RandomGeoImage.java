package com.practice.guessandtell.dto;

public record RandomGeoImage(
        String imageUrl,
        double latitude,
        double longitude
) {
}
