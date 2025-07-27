// src/components/CardPlayZone.js
import React, { useState, useRef, useEffect } from 'react';

import { useUIDisplay } from '../context/UIDisplayContext.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import { useZoneRefs } from '../context/RefContext';
import { useThrowAlert } from '../hooks/useThrowAlert.js';

import AnimatedCard from './AnimatedCard';
import PlayCardAnimation from '../animations/PlayCardAnimation.jsx';
import BiddingPanel from './BiddingPanel';
import BidTypePanel from './BidTypePanel.jsx';

import { getPositionMap } from '../utils/PositionUtils';
import { delay } from '../utils/TimeUtils';

import { dealCardsClockwise } from '../animations/DealAnimation';

import '../css/CardPlayZone.css';

/*
* CardPlayZone manages the central play area of the
* Bid Whist card game. It handles:
*   - Card play animations (deal, play, collect, clear)
*   - Drag-and-drop logic for playing cards
*   - Updating card positions for animation accuracy
*   - Triggering UI components for bidding and bid type
*   - Communicating animation progress with the backend
*/

export default function CardPlayZone({ dropZoneRef, yourTrickRef, theirTrickRef, onCardPlayed }) {

  const {
    setHandFor,
    setShowHands,
    setShowBidding,
    deckPosition,
    setDeckPosition,
    setPlayedCard,
    setPlayedCardPosition,
    playedCardsByDirection,
    setPlayedCardsByDirection,
    animatedCards,
    setAnimatedCards,
    showAnimatedCards,
    setShowAnimatedCards,
    setBidPhase,
    setShowFinalizeBid,
    animationQueue,
    setTeamATricks,
    setTeamBTricks,
  } = useUIDisplay();


  const {
    debugLog: positionLog,
    playerName,
    viewerPosition,
    backendPositions,
    positionToDirection,
  } = usePositionContext();


  const { gameId, players, setKitty, bids, winningPlayerName, setWinningBid, bidWinnerPos } =
    useGameState();

  const [isOver, setIsOver] = useState(false); // Tracks if drag is over drop zone
  const [lastAnimation, setLastAnimation] = useState(null); // Prevents duplicate animation runs
  const [playAnimations, setPlayAnimations] = useState([]); // Currently running card play animations
  const [playedCardPosition, setPlayedCardPositions] = useState({}); // Positional cache of dropped cards

  const throwAlert = useThrowAlert();
  const localRef = useRef(); // Main container ref for sizing/layout
  const { register, get } = useZoneRefs(); // Shared card zone registry
  const API = import.meta.env.VITE_API_URL; // Server endpoint

  /* Handles queued game animations like DEAL, PLAY, COLLECT, etc. */
  useEffect(() => {
    if (!animationQueue || animationQueue.length === 0) return;

    const animation = animationQueue[0];
    if (!animation || animation.id === lastAnimation) return;

    setLastAnimation(animation.id);

    const runAnimation = async () => {

      if (animation.type === 'DEAL') {
        const requiredRefs = [
          'hand-south',
          'hand-west',
          'hand-north',
          'hand-east',
          'play-south',
          'play-west',
          'play-north',
          'play-east',
        ];
        const refsReady = requiredRefs.every((dir) => get(dir)?.current);

        if (!refsReady) {
          console.log('Refs not ready, retrying runAnimation in 50ms...');
          setTimeout(runAnimation, 50);
          return;
        }

        const cards = players.flatMap((p) =>
          (p.hand || []).map((card) => ({ ...card, owner: p.position }))
        );

        const playerPositions = players.map((p) => p.position);
        const positionMap = getPositionMap(backendPositions, viewerPosition);

        setTimeout(() => {
          dealCardsClockwise(
            playerPositions,
            cards,
            positionMap,
            () => setShowHands(true),
            get,
            deckPosition,
            setAnimatedCards,
            setShowAnimatedCards,
            setBidPhase
          );
        }, 50);
      }

      if (animation.type === 'PLAY') {
        const { card, player } = animation;
        const direction = positionToDirection[player];
        const fromRef = get(`card-origin-${direction}`);
        const toRef = get(`play-${direction}`);

        console.log('PLAY animation triggered');
        console.log('→ direction:', direction);
        console.log('→ fromRef:', fromRef?.current);
        console.log('→ toRef:', toRef?.current);

        if (!fromRef?.current || !toRef?.current) {
          console.warn('Missing refs for PLAY animation', { fromRef, toRef });
          setTimeout(() => {
            animationQueue.unshift(animation); // retry animation
          }, 100);
          return;
        }

        setPlayAnimations((prev) => [
          ...prev,
          {
            id: `${card.cardImage}-${player}`,
            card,
            fromRef,
            toRef,
            direction,
          },
        ]);

        await delay(800); // wait for animation
        setPlayedCardsByDirection((prev) => ({ ...prev, [direction]: card }));
      }

      if (animation.type === 'COLLECT') {
        const { cardList, winningTeam } = animation;
        const myTeam = viewerPosition === 'P1' || viewerPosition === 'P3' ? 'A' : 'B';
        const targetRef = winningTeam === myTeam ? yourTrickRef : theirTrickRef;

        for (let card of cardList) {
          const dir = positionToDirection[card.owner];
          const from = get(`play-${dir}`);
          const to = targetRef;

          if (!from?.current || !to?.current) {
            console.warn('Missing ref for trick collect animation:', { from, to });
            continue;
          }

          setAnimatedCards((prev) => [
            ...prev,
            {
              id: `collect-${card.cardImage}-${card.owner}`,
              ...card,
              cardImage: 'Deck_Back.png',
              from,
              to,
            },
          ]);
        }

        /* increment the appropriate team's trick count */
        if (winningTeam === 'A') {
          setTeamATricks((prev) => prev + 1);
        } else {
          setTeamBTricks((prev) => prev + 1);
        }

        /* after animation completes, clear board state */
        setTimeout(
          () => {
            setAnimatedCards([]);
            setPlayedCardsByDirection({
              north: null,
              south: null,
              east: null,
              west: null,
            });
          },
          cardList.length * 150 + 300
        );
      }

      if (animation.type === 'CLEAR') {
        setTeamATricks(0);
        setTeamBTricks(0);
        setWinningBid(null);
      }

      if (animation.type === 'UPDATE_CARDS') {
        try {
          const res = await fetch(`${API}/game/update-cards`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ gameId, player: viewerPosition }),
          });

          const data = await res.json();
          if (!res.ok || !data.players || !data.kitty) {
            throwAlert(data, 'warninge');
            console.error('[CardPlayZone] Invalid update response:', data);
            return;
          }

          data.players.forEach(({ position, hand }) => {
            const direction = positionToDirection[position];
            console.log(`[UPDATE_CARDS] setting hand for ${direction}:`, hand);
            setHandFor(direction, hand);
          });

          setKitty(data.kitty);
        } catch (err) {
          console.error('[CardPlayZone] Error updating card data:', err);
        }
      }

      /* === SHOW_WINNER animation: (not implemented yet) === */
      if (animation.type === 'SHOW_WINNER') {
        // TODO: implement winner reveal animation or panel
      }

      /* === QUIT_GAME animation: (not implemented yet) === */
      if (animation.type === 'QUIT_GAME') {
        // TODO: implement winner reveal animation or panel
      }

      /* Notify backend that the animation has finished */
      const res2 = await fetch(`${API}/game/pop-animation`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          gameId,
          player: viewerPosition,
          animationId: animation.id,
        }),
      });
      if (!res2.ok) {
        let data;
        try {
          data = await res2.json();
        } catch (e) {
          data = { message: 'Something went wrong' };
        }
        throwAlert(data, 'error');
      }
    };

    runAnimation();
  }, [animationQueue, lastAnimation, players, viewerPosition, backendPositions, deckPosition]);

  /* Tracks and updates the screen position of each played card */
  useEffect(() => {
    Object.entries(playedCardsByDirection).forEach(([dir, card]) => {
      if (!card) return;
      const playRefObj = get(`play-${dir}`);
      const containerRect = document.querySelector('.floating-card-layer')?.getBoundingClientRect();

      if (!playRefObj?.current || !containerRect) return;

      const playRect = playRefObj.current.getBoundingClientRect();
      const x = playRect.left + playRect.width / 2 - containerRect.left;
      const y = playRect.top + playRect.height / 2 - containerRect.top;

      setPlayedCardPositions((prev) => ({ ...prev, [dir]: { x, y } }));
    });
  }, [playedCardsByDirection]);

  /* Updates the deck center point relative to its parent container */
  useEffect(() => {
    const updatePositions = () => {
      const bounds = localRef.current?.getBoundingClientRect();
      const parentBounds = document.querySelector('.floating-card-layer')?.getBoundingClientRect();

      if (bounds && parentBounds) {
        const centerX = bounds.left + bounds.width / 2 - parentBounds.left;
        const centerY = bounds.top + bounds.height / 2 - parentBounds.top;
        setDeckPosition({ x: centerX, y: centerY });
      }
    };

    updatePositions();
    window.addEventListener('resize', updatePositions);
    return () => window.removeEventListener('resize', updatePositions);
  }, []);

  /* Registers a directional ref for the play zone container */
  useEffect(() => {
    if (!viewerPosition || !positionToDirection) return;
    const direction = positionToDirection[viewerPosition];
    if (!direction) return;
    register(`CardPlayZone-${direction}`, localRef);
  }, [viewerPosition, positionToDirection, register]);

  /* Triggers Finalize Bid panel if bidding phase is complete and viewer won */
  useEffect(() => {
    const bidsComplete = bids?.length === 4;
    console.log(`Bid length = ${bids?.length}`);
    const iWonBid = bidWinnerPos === viewerPosition;
    console.log(`Winner Positions: ${bidWinnerPos} Player Position ${viewerPosition}`);
    setShowFinalizeBid(bidsComplete && iWonBid);
  }, [bids, winningPlayerName, bidWinnerPos, playerName]);

  /* handleDrop: Processes a card being dropped into play by the player */
  const handleDrop = async (e) => {
    e.preventDefault();
    const rawData = e.dataTransfer.getData('application/json');
    if (!rawData || !isOver) return;
    const card = JSON.parse(rawData);

    const res3 = await fetch(`${API}/game/play`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ gameId, player: viewerPosition, card }),
    });

    const data = await res3.json();

    if (!res3.ok) {
      throwAlert(data, 'info');
      return;
    }

    if (positionToDirection[viewerPosition] === 'south') {
      setPlayedCard(card);
      onCardPlayed?.(card);
    }

    const zoneRect = dropZoneRef.current?.getBoundingClientRect();
    const cardZoneRect = localRef.current?.getBoundingClientRect();
    if (zoneRect && cardZoneRect) {
      setPlayedCardPosition({
        x: zoneRect.left - cardZoneRect.left,
        y: zoneRect.top - cardZoneRect.top,
      });
    }

    setIsOver(false);
  };


  return (
    <div ref={localRef} className="card-play-zone">
      {positionToDirection[viewerPosition] === 'south' && (
        <div
          ref={dropZoneRef}
          className={`drop-zone south ${isOver ? 'highlight' : ''}`}
          onDragOver={(e) => e.preventDefault()}
          onDragEnter={() => setIsOver(true)}
          onDragLeave={() => setIsOver(false)}
          onDrop={handleDrop}
        ></div>
      )}

      <div
        ref={(el) => el && register('play-north', { current: el })}
        className="play-slot north"
      />
      <div
        ref={(el) => el && register('play-south', { current: el })}
        className="play-slot south"
      />
      <div ref={(el) => el && register('play-east', { current: el })} className="play-slot east" />
      <div ref={(el) => el && register('play-west', { current: el })} className="play-slot west" />

      <div className="floating-card-layer">
        <BiddingPanel closeBidding={() => setShowBidding(false)} />
        <BidTypePanel closeBidTypePanel={() => setShowFinalizeBid(false)} />

        {Object.entries(playedCardsByDirection).map(([dir, card]) => {
          if (!card) return null;
          const playRefObj = get(`play-${dir}`);
          const containerRect = document
            .querySelector('.floating-card-layer')
            ?.getBoundingClientRect();
          if (!playRefObj?.current || !containerRect) return null;
          const playRect = playRefObj.current.getBoundingClientRect();
          const x = playRect.left + playRect.width / 2 - containerRect.left;
          const y = playRect.top + playRect.height / 2 - containerRect.top;

          return (
            <img
              key={dir}
              src={`/static/img/deck/${card.cardImage}`}
              alt={`Played ${dir}`}
              className="card-img"
              style={{
                position: 'absolute',
                left: x,
                top: y,
                transform: 'translate(-50%, -50%)',
                zIndex: 5,
              }}
            />
          );
        })}

        {playAnimations.map((anim) => (
          <PlayCardAnimation
            key={anim.id}
            card={anim.card}
            fromRef={anim.fromRef}
            toRef={anim.toRef}
            onComplete={() => {
              setPlayAnimations((prev) => prev.filter((a) => a.id !== anim.id));
              setPlayedCardsByDirection((prev) => ({
                ...prev,
                [anim.direction]: anim.card,
              }));
            }}
          />
        ))}

        {showAnimatedCards &&
          animatedCards.map((card, i) => (
            <AnimatedCard key={card.id} card={card} from={card.from} to={card.to} zIndex={i} />
          ))}
      </div>
    </div>
  );
}
