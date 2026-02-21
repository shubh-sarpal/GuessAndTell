package com.practice.guessandtell.controller;

import com.practice.guessandtell.dto.*;
import com.practice.guessandtell.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<GameResponse> createGame(
            @Valid @RequestBody CreateGameRequest request
            ){
        GameResponse response = gameService.createGame(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{code}/join")
    public ResponseEntity<JoinGameResponse> joinGame(
            @PathVariable String code,
            @Valid @RequestBody JoinGameRequest request
    ){
        JoinGameResponse response = gameService.joinGame(code, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{code}/start")
    public ResponseEntity<GameResponse> startGame(@PathVariable String code){
        return ResponseEntity.ok(gameService.startGame(code));
    }

    @PostMapping("/{code}/guess")
    public ResponseEntity<Void> submitGuess(
            @PathVariable String code,
            @Valid @RequestBody SubmitGuessRequest request
            ){
        gameService.submitGuess(
                code,
                request.playerId(),
                request.latitude(),
                request.longitude(),
                request.year()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{code}")
    public ResponseEntity<GameStateResponse> getGameState(
            @PathVariable String code
    ){
        return ResponseEntity.ok(gameService.getGameState(code));
    }
}
