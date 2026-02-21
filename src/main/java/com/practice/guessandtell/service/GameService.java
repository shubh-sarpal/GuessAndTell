package com.practice.guessandtell.service;

import com.practice.guessandtell.dto.*;
import com.practice.guessandtell.enums.GameStatus;
import com.practice.guessandtell.model.Game;
import com.practice.guessandtell.model.Guess;
import com.practice.guessandtell.model.Player;
import com.practice.guessandtell.model.Round;
import com.practice.guessandtell.repository.GameRepository;
import com.practice.guessandtell.repository.GuessRepository;
import com.practice.guessandtell.repository.PlayerRepository;
import com.practice.guessandtell.repository.RoundRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final RoundRepository roundRepository;
    private final GuessRepository guessRepository;

    public GameResponse createGame(CreateGameRequest request) {
        Game game = new Game();
        game.setCode(generateJoinCode());
        game.setMode(request.mode());
        game.setStatus(GameStatus.WAITING);
        game.setMaxPlayers(request.maxPlayers());
        game.setCreatedAt(Instant.now());

        gameRepository.save(game);


        return new GameResponse(
                game.getId(),
                game.getCode(),
                game.getStatus().name()
        );
    }

    public JoinGameResponse joinGame(String code, JoinGameRequest request) {
        Game game = gameRepository.findByCode(code).orElseThrow(
                ()-> new RuntimeException("Game with code " + code + " not found")
        );

        if(game.getStatus() !=GameStatus.WAITING){
            throw new RuntimeException("Game already started");
        }
        if(game.getPlayers().size()>= game.getMaxPlayers()){
            throw new RuntimeException("Game is full");
        }

        Player player = new Player();
        player.setUsername(request.username());
        player.setTotalScore(0);
        player.setGame(game);

        playerRepository.save(player);

        game.getPlayers().add(player);
        return new JoinGameResponse(player.getId());
    }

    private String generateJoinCode(){
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public GameResponse startGame(String code) {
        Game game = gameRepository.findByCode(code).orElseThrow(()-> new RuntimeException("Game with code " + code + " not found"));

        if(game.getStatus() !=GameStatus.WAITING){
            throw new RuntimeException("Game already started");
        }
        if(game.getPlayers().size()< 2){
            throw new RuntimeException("Game needs atleast 2 people");
        }
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCurrentRoundNumber(1);

        Round firstRound = createRound(game, 1);
        game.getRounds().add(firstRound);
        game.setCurrentRound(firstRound);
        gameRepository.save(game);
        return mapToResponse(game);
    }

    private Round createRound(Game game, int roundNumber){
        Round round = new Round();
        round.setGame(game);

        //TODO Logic for game round properties setup
        round.setImageUrl("https://example.com/sample.jpg");
        round.setActualLatitude(28.6139 + roundNumber);
        round.setActualLongitude(77.834 + roundNumber);
        round.setActualYear(2018 + roundNumber);

        round.setStartedAt(Instant.now());
        return round;
    }

    public void nextRound(String code){

        Game game = gameRepository.findByCode(code).orElseThrow(()-> new RuntimeException("Game with code " + code + " not found"));
        if(game.getStatus() !=GameStatus.IN_PROGRESS){
            throw new RuntimeException("Game not active");
        }
        int current = game.getCurrentRoundNumber();

        if(current>=game.getTotalRounds()){
            game.setStatus(GameStatus.FINISHED);
            gameRepository.save(game);
            return;
        }
        int nextRoundNumber = current+1;
        Round nextRound = createRound(game, nextRoundNumber);
        game.setCurrentRound(nextRound);
        game.getRounds().add(nextRound);
        game.setCurrentRoundNumber(nextRoundNumber);
        gameRepository.save(game);
    }

    private GameResponse mapToResponse(Game game){
        return new GameResponse(
                game.getId(),
                game.getCode(),
                game.getStatus().name()
        );
    }

    public void submitGuess(String code, UUID playerId, double lat, double lng, int year){
        Game game = gameRepository.findByCode(code).orElseThrow(
                () -> new RuntimeException("Game with code " + code + " not found")
        );



        if(game.getStatus() !=GameStatus.IN_PROGRESS){
            throw new RuntimeException("Game not active");
        }

        Player player = playerRepository.findById(playerId).orElseThrow(
                () -> new RuntimeException("Player with id " + playerId + " not found")
        );

        if(!player.getGame().getId().equals(game.getId())){
            throw new RuntimeException("Player with id not part of this game : " + playerId + " .");
        }
        Round currentRound = game.getCurrentRound();

        if(guessRepository.existsByRoundAndPlayer(currentRound, player)){
            throw new RuntimeException("Player already guessed this round");
        }

        Guess guess = new Guess();
        guess.setRound(currentRound);
        guess.setPlayer(player);
        guess.setGuessedLatitude(lat);
        guess.setGuessedLongitude(lng);
        guess.setGuessedYear(year);

        double distance = calculateDistance(
                lat,lng, currentRound.getActualLatitude(),currentRound.getActualLongitude()
        );

        int yearDiff = Math.abs(year - currentRound.getActualYear());

        guess.setDistanceKm(distance);
        guess.setYearDifference(yearDiff);

        int score = calculateScore(distance, yearDiff);
        guess.setScore(score);

        player.setTotalScore(player.getTotalScore()+score);

        guessRepository.save(guess);
        checkAndAdvanceRound(game, currentRound);
    }

    private void checkAndAdvanceRound(Game game, Round currentRound) {
        long guessCount = guessRepository.countByRound(currentRound);

        if(guessCount < game.getPlayers().size()){
            return;
        }

        currentRound.setEndedAt(Instant.now());
        nextRound(game.getCode());
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2){


        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);

        double c =  2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return R*c;
    }

    private int calculateScore(double distance, int yearDifference){

        int distanceScore = Math.max(0, 5000 - (int)(distance * 10));
        int yearScore = Math.max(0, 1000 - (yearDifference * 50));

        return distanceScore + yearScore;
    }

    public GameStateResponse getGameState(String code) {

        Game game = gameRepository.findByCode(code).orElseThrow(
                ()-> new RuntimeException("Game with code " + code + " not found")
        );

        String imageUrl = null;

        if(game.getCurrentRound()!=null){
            imageUrl = game.getCurrentRound().getImageUrl();
        }

        List<PlayerScoreResponse> leaderboard = game.getPlayers()
                .stream()
                .sorted((p1,p2)-> Integer.compare(p2.getTotalScore(), p1.getTotalScore()))
                .map(player -> new PlayerScoreResponse(
                        player.getUsername(),
                        player.getTotalScore()
                ))
                .toList();

        return new GameStateResponse(
                game.getStatus().name(),
                game.getCurrentRoundNumber(),
                game.getTotalRounds(),
                imageUrl,
                leaderboard
        );
    }
}
