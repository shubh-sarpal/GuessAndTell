package com.practice.guessandtell.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private String imageUrl;

    private double actualLatitude;
    private double actualLongitude;

    private int actualYear;

    private Instant startedAt;
    private Instant endedAt;
}
