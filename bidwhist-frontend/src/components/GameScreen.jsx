// src/components/GameScreen.jsx
import React from 'react';
import { useRef, useImperativeHandle, forwardRef } from 'react';
import '../css/GameScreen.css';
import CardPlayZone from './CardPlayZone';
import PlayerZone from './PlayerZone';


export default function GameScreen({ gameState }) {
    const GameScreen = forwardRef(({ gameState }, ref) => {
        const screenRef = useRef();

        useImperativeHandle(ref, () => ({
            getPosition: () => screenRef.current?.getBoundingClientRect()
        }));

        return (
            <div ref={screenRef} className="game-screen">

                <div className="center-area">
                    <CardPlayZone playerPosition="south" onCardPlayed={(card) => console.log("Played:", card)} />
                </div>
                <div className="bottom-bar">
                    <PlayerZone gameState={gameState} playerName={gameState?.playerName || "You"} />
                </div>
            </div>
        );
    })}
