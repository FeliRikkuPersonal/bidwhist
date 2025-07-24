import { clearAllGameData } from "./ClearData";

const handleQuit = () => {
    
  if (confirm('Are you sure you want to quit? This will clear all game data.')){
        clearAllGameData();
        window.location.reload();
        window.location.href = '/';
      }
    }

export default handleQuit;