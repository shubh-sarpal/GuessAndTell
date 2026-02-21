package com.practice.guessandtell.repository;

import com.practice.guessandtell.model.Guess;
import com.practice.guessandtell.model.Player;
import com.practice.guessandtell.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuessRepository extends JpaRepository<Guess, UUID> {

    long countByRound(Round round);

    boolean existsByRoundAndPlayer(Round round, Player player);
}
