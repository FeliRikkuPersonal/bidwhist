import Reach from 'react';
import '../BiddingPanel.css';

export default function BiddingPanel({ placeBid, closeBidding }) {
    return (
        <div className="bidding-overlay">
            <div className="bidding-panel">
                <h2>Place Your Bid</h2>
                {/* Bidding form UI goes here */}
                <p><input
                    type={"textfield"}
                    placeholder={"Enter your bid"}
                    className={"index-input-box short-box;"}></input></p>
                <div className="settings-actions">
                    <button className="index-button settings-button" onClick={placeBid}>Set Bid</button>
                    <button className="index-button settings-button" onClick={closeBidding}>Close</button>
                </div>
            </div>
        </div>
    );
}