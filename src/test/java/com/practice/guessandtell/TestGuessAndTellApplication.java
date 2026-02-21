package com.practice.guessandtell;

import org.springframework.boot.SpringApplication;

public class TestGuessAndTellApplication {

    public static void main(String[] args) {
        SpringApplication.from(GuessAndTellApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
