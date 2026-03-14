import {useNavigate} from "react-router-dom";
import {createGame} from "../api/gameApi.ts";

function Home(){
    const navigate = useNavigate();

    const handleCreate = async () =>{
        try{
            const game = await createGame();
            navigate(`/lobby/${game.code}`);
        }catch (err){
            alert("Failed to create game");
            console.error(err);
        }
    };
    return(
        <div>
            <h1>Guess & Tell</h1>
            <button onClick={handleCreate}>Create game</button>
        </div>
    )
}

export default Home;