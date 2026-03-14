import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { joinGame, startGame, getGameState } from "../api/gameApi";
import type { GameStateResponse } from "../types/game";

function Lobby() {
    const { code } = useParams();
    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [playerId, setPlayerId] = useState<string | null>(null);
    const [gameState, setGameState] = useState<GameStateResponse | null>(null);

    // Poll game state
    useEffect(() => {
        if (!code) return;

        const interval = setInterval(async () => {
            const state = await getGameState(code);
            setGameState(state);

            if (state.status === "IN_PROGRESS") {
                navigate(`/game/${code}`);
            }
        }, 2000);

        return () => clearInterval(interval);
    }, [code, navigate]);

    const handleJoin = async () => {
        if (!code) return;

        try {
            const res = await joinGame(code, username);
            localStorage.setItem("playerId", res.playerId);
            localStorage.setItem("username", username);
            setPlayerId(res.playerId);
        } catch (err) {
            alert("Failed to join");
            console.error(err);
        }
    };

    const handleStart = async () => {
        if (!code) return;
        await startGame(code);
    };

    return (
        <div>
            <h2>Lobby - Code: {code}</h2>

            {!playerId ? (
                <>
                    <input
                        placeholder="Enter username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <button onClick={handleJoin}>Join</button>
                </>
            ) : (
                <p>Joined as {username}</p>
            )}

            <h3>Players</h3>
            <ul>
                {gameState?.leaderboard.map((p) => (
                    <li key={p.username}>{p.username}</li>
                ))}
            </ul>

            <button onClick={handleStart}>Start Game</button>
        </div>
    );
}

export default Lobby;