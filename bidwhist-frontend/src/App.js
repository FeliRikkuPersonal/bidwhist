import './App.css';
import ModeSelector from "./components/ModeSelector";

function App() {
  return (
    <div className="index-wrapper">
        <div className="index-container">
            <h1 className="h1-welcome">Welcome to Bid Whist Online!</h1>
            <ModeSelector />
        </div>
    </div>
  );
}

export default App;