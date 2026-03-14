import axios from 'axios';
import type {
    GameResponse,
    JoinGameResponse,
    GameStateResponse
} from "../types/game.ts";

const API = axios.create({
    baseURL: "http://localhost:8080/api",
});

export const createGame = async(): Promise<GameResponse> =>{
    const res = await API.post("/games",{
        mode: "PLAYER_UPLOAD",
        maxPlayers: 4
    });
    return res.data;
}

export const joinGame = async(
    code: string,
    username: string
): Promise<JoinGameResponse> =>{
    const res = await API.post(`/games/${code}/join`, {username});
    return res.data;
};

export const startGame = async(code: string): Promise<GameResponse> =>{
    const res = await API.post(`/games/${code}/start`);
    return res.data;
};

export const getGameState = async(
    code: string
): Promise<GameStateResponse> =>{
    const res = await API.get(`/games/${code}`);
    return res.data;
};

export const submitGuess = async(
    code: string,
    playerId: string,
    latitude: number,
    longitude: number,
    year: number
): Promise<void> =>{
    await API.post(`/games/${code}/guess`,{
        playerId,
        latitude,
        longitude,
        year
    })
};