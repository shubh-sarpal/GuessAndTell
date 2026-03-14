import {BrowserRouter, Route, Routes} from "react-router-dom";
import Home from "./pages/Home.tsx";
import Lobby from "./pages/Lobby.tsx";
import Game from "./pages/Game.tsx";

function App(){
    return(
       <BrowserRouter>
           <Routes>
               <Route path="/" element={<Home />} />
               <Route path="/lobby/:code" element={<Lobby />} />
               <Route path="/game/:code" element={<Game />} />
           </Routes>
       </BrowserRouter>
    );
}

export default App
