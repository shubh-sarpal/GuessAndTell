export interface GameResponse{
    gameId: string;
    code: string;
    status: string;
}

export interface JoinGameResponse{
    playerId: string;
}

export interface PlayerScoreResponse{
    username: string;
    totalScore: number;
}

export interface GameStateResponse{
    status: string;
    currentRound: number;
    totalRounds: number;
    imageUrl: string;
    leaderboard: PlayerScoreResponse[];

}