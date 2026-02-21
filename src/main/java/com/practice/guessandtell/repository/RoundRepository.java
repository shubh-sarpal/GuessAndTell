package com.practice.guessandtell.repository;

import com.practice.guessandtell.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RoundRepository extends JpaRepository<Round, UUID> {
}
