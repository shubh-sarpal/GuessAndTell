package com.practice.guessandtell.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Guess {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Round round;

    @ManyToOne
    private Player player;

    private double guessedLatitude;
    private double guessedLongitude;

    private int guessedYear;

    private double distanceKm;
    private int yearDifference;

    private int score;
}
