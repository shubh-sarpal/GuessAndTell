import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getGameState, submitGuess } from "../api/gameApi";
import type { GameStateResponse } from "../types/game";
import { MapContainer, TileLayer, Marker, useMapEvents } from "react-leaflet";
import L from "leaflet";

delete (L.Icon.Default.prototype as any)._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png",
    iconUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
    shadowUrl:
        "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
});

function Game() {
    const { code } = useParams();

    const [gameState, setGameState] = useState<GameStateResponse | null>(null);
    const [position, setPosition] = useState<any>(null);
    // const [latitude, setLatitude] = useState("");
    // const [longitude, setLongitude] = useState("");
    const [year, setYear] = useState("");
    // const [playerId, setPlayerId] = useState("");
    const [playerId, setPlayerId] = useState<string | null>(null);

    useEffect(() => {
        const storedPlayerId = localStorage.getItem("playerId");
        if(storedPlayerId){
            setPlayerId(storedPlayerId);
        }
    }, []);

    // Poll game state every 2s
    useEffect(() => {
        if (!code) return;

        const interval = setInterval(async () => {
            const state = await getGameState(code);
            setGameState(state);
        }, 2000);

        return () => clearInterval(interval);
    }, [code]);

    const handleSubmit = async () => {
        if (!code || !playerId){
            alert("You are not joined in this game");
            return;
        }

        await submitGuess(
            code,
            playerId,
            position.lat,
            position.lng,
            Number(year)
        );

        alert("Guess submitted!");
    };

    function LocationMarker({setPosition}: any){
        useMapEvents({
            click(e){
                setPosition(e.latlng);
            },
        });
        return null;
    }

    return (
        <div>
            <h2>Round {gameState?.currentRound}</h2>

            <img
                src={gameState?.imageUrl}
                alt="round"
                width="400"
            />

            <div>
                {/*<input*/}
                {/*    placeholder="Player ID"*/}
                {/*    value={playerId}*/}
                {/*    onChange={(e) => setPlayerId(e.target.value)}*/}
                {/*/>*/}

                <div style={{height: "400px", width: "100%"}}>
                    <MapContainer
                        center={[20,0]}
                        zoom={2}
                        style={{ height: "100%", width: "100%" }}>
                        <TileLayer
                            url = "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"/>
                        <LocationMarker setPosition={setPosition} />
                        {position && <Marker position = {position} />}
                    </MapContainer>
                </div>

                <input
                    placeholder="Year"
                    value={year}
                    onChange={(e) => setYear(e.target.value)}
                />

                <button onClick={handleSubmit}>Submit Guess</button>
            </div>

            <h3>Leaderboard</h3>
            <ul>
                {gameState?.leaderboard.map((p) => (
                    <li key={p.username}>
                        {p.username} - {p.totalScore}
                    </li>
                ))}
            </ul>
        </div>
    );
}

export default Game;