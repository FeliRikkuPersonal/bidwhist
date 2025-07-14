// src/components/CardPlayZone.js
import { useState, useRef, useEffect } from 'react';
import { useZoneRefs } from '../context/RefContext';
import AnimatedCard from './AnimatedCard';
import { getPositionMap } from '../utils/PositionUtils';
import BiddingPanel from './BiddingPanel';
import { delay } from '../utils/TimeUtils';
import { dealCardsClockwise } from '../animations/DealAnimation';
import PlayCardAnimation from '../animations/PlayCardAnimation.jsx';
import '../css/CardPlayZone.css';
import { useUIDisplay } from '../context/UIDisplayContext.jsx';
import { useGameState } from '../context/GameStateContext.jsx';
import { usePositionContext } from '../context/PositionContext.jsx';
import BidTypePanel from './BidTypePanel.jsx';


export default function CardPlayZone({ dropZoneRef, yourTrickRef, theirTrickRef, onCardPlayed }) {
    const {
        setShowHands,
        setShowBidding,
        deckPosition,
        setDeckPosition,
        setPlayedCard,
        setPlayedCardPosition,
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
        playerName,
        viewerPosition,
        backendPositions,
        positionToDirection,
    } = usePositionContext();

    const {
        gameId,
        players,
        bids,
        winningPlayerName,
        bidWinnerPos,
    } = useGameState();

    const [isOver, setIsOver] = useState(false);
    const [lastAnimation, setLastAnimation] = useState(null);
    const [playAnimations, setPlayAnimations] = useState([]);
    const [playedCardPosition, setPlayedCardPositions] = useState({});
    const [playedCardsByDirection, setPlayedCardsByDirection] = useState({
        north: null,
        south: null,
        east: null,
        west: null
    });

    const localRef = useRef();
    const { register, get } = useZoneRefs();


    // --- useEffect for Animations ---
    /* Manages animation queue and handles running different types of animations like DEAL, PLAY, and COLLECT. */
    useEffect(() => {
        if (!animationQueue || animationQueue.length === 0) return;
        const animation = animationQueue[0];
        if (!animation || animation.id === lastAnimation) return;
        setLastAnimation(animation.id);

        const runAnimation = async () => {
            if (animation.type === 'DEAL') {

                // Ensures all refs are ready before starting the DEAL animation
                const requiredRefs = ['hand-south', 'hand-west', 'hand-north', 'hand-east', 'play-south', 'play-west', 'play-north', 'play-east'];
                const refsReady = requiredRefs.every(dir => get(dir)?.current);

                if (!refsReady) {
                    console.log('Refs not ready, retrying runAnimation in 50ms...');
                    setTimeout(() => {
                        runAnimation();
                    }, 50);
                    return;
                }

                const cards = players.flatMap(p => (p.hand || []).map(card => ({ ...card, owner: p.position })));
                const playerPositions = players.map(p => p.position);
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

                // Handle PLAY animation for the card
                const fromRef = get(`card-origin-${direction}`);
                const toRef = get(`play-${direction}`);

                console.log("🂠 PLAY animation triggered");
                console.log("→ direction:", direction);
                console.log("→ fromRef:", fromRef?.current);
                console.log("→ toRef:", toRef?.current);

                if (!fromRef?.current || !toRef?.current) {
                    console.warn('❌ Missing refs for PLAY animation', { fromRef, toRef });
                    setTimeout(() => {
                        animationQueue.unshift(animation);  // Retry if refs are not ready
                    }, 100);
                    return;
                }

                setPlayAnimations(prev => [...prev, {
                    id: `${card.cardImage}-${player}`,
                    card,
                    fromRef,
                    toRef,
                    direction,
                }]);

                await delay(800);
                setPlayedCardsByDirection(prev => ({ ...prev, [direction]: card }));
            }


            if (animation.type === 'COLLECT') {
                // Handling COLLECT animation for winning cards and updating the trick count
                const { cardList, winningTeam } = animation;
                const myTeam = viewerPosition === 'P1' || viewerPosition === 'P3' ? 'A' : 'B';
                const targetRef = (winningTeam === myTeam) ? yourTrickRef : theirTrickRef;

                for (let card of cardList) {
                    const dir = positionToDirection[card.owner];
                    const from = get(`play-${dir}`);
                    const to = targetRef;

                    if (!from?.current || !to?.current) {
                        console.warn('Missing ref for trick collect animation:', { from, to });
                        continue;
                    }

                    setAnimatedCards(prev => [
                        ...prev,
                        {
                            id: `collect-${card.cardImage}-${card.owner}`,
                            ...card,
                            cardImage: 'Deck_Back.png',     // Use back of card for trick collection animation
                            from,
                            to,
                        }
                    ]);
                }

                if (animation.type === 'COLLECT') {
                    
                }

                // Increment trick count
                if (winningTeam === 'A') {
                    setTeamATricks(prev => prev + 1);
                } else {
                    setTeamBTricks(prev => prev + 1);
                }

                setTimeout(() => {
                    setAnimatedCards([]);   // Clear animations after completion
                    setPlayedCardsByDirection({ north: null, south: null, east: null, west: null });
                }, cardList.length * 150 + 300);
            }



            // Notify backend that animation has completed
            await fetch('/api/game/pop-animation', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ gameId, player: viewerPosition, animationId: animation.id }),
            });
        };

        runAnimation();
    }, [animationQueue, lastAnimation, players, viewerPosition, backendPositions, deckPosition]);


    // --- useEffect for Played Card Positions ---
    /* Calculates and updates the position of each played card based on its direction. */
    useEffect(() => {
        Object.entries(playedCardsByDirection).forEach(([dir, card]) => {
            if (!card) return;
            const playRefObj = get(`play-${dir}`);
            const containerRect = document.querySelector('.floating-card-layer')?.getBoundingClientRect();
            if (!playRefObj?.current || !containerRect) return;
            const playRect = playRefObj.current.getBoundingClientRect();
            const x = playRect.left + playRect.width / 2 - containerRect.left;
            const y = playRect.top + playRect.height / 2 - containerRect.top;
            setPlayedCardPositions(prev => ({ ...prev, [dir]: { x, y } }));
        });
    }, [playedCardsByDirection]);


    // --- useEffect for Updating Deck Position ---
    /* Updates the deck position relative to the container. Handles resizing events. */
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


    // --- useEffect for Registering Play Zone Refs ---
    /* Registers refs for the card play zone, based on the player's position. */
    useEffect(() => {
        if (!viewerPosition || !positionToDirection) return;
        const direction = positionToDirection[viewerPosition];
        if (!direction) return;
        register(`CardPlayZone-${direction}`, localRef);
    }, [viewerPosition, positionToDirection, register]);


    // --- useEffect for Finalizing Bid ---
    /* Checks if the bid phase is complete and if the player won the bid, then shows the finalize bid panel. */
    useEffect(() => {
        const bidsComplete = (bids?.length === 4);
        console.log(`Bid length = ${bids?.length}`);
        const iWonBid = (bidWinnerPos === viewerPosition);
        console.log(`Winner Positions: ${bidWinnerPos} Player Position ${viewerPosition}`);
        setShowFinalizeBid(bidsComplete && iWonBid);
    }, [bids, winningPlayerName, playerName]);


    // --- handleDrop Function ---
    /* Handles the drop event for cards during play. Updates the game state and triggers the animation for the played card. */
    const handleDrop = async (e) => {
        e.preventDefault();
        const rawData = e.dataTransfer.getData('application/json');
        if (!rawData || !isOver) return;
        const card = JSON.parse(rawData);

        await fetch('/api/game/play', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ gameId, player: viewerPosition, card })
        });

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



    // --- JSX Rendering ---
    /* Renders the play zone, bidding panel, and animated cards based on the current game state. */
    return (
        <div ref={localRef} className="card-play-zone">
            {positionToDirection[viewerPosition] === 'south' && (
                <div ref={dropZoneRef} className={`drop-zone south ${isOver ? 'highlight' : ''}`} onDragOver={(e) => e.preventDefault()} onDragEnter={() => setIsOver(true)} onDragLeave={() => setIsOver(false)} onDrop={handleDrop}></div>
            )}
            <div ref={(el) => el && register(`play-north`, { current: el })} className="play-slot north"></div>
            <div ref={(el) => el && register(`play-south`, { current: el })} className="play-slot south"></div>
            <div ref={(el) => el && register(`play-east`, { current: el })} className="play-slot east"></div>
            <div ref={(el) => el && register(`play-west`, { current: el })} className="play-slot west"></div>


            <div className="floating-card-layer">
                <BiddingPanel closeBidding={() => setShowBidding(false)} />
                <BidTypePanel closeBidTypePanel={() => setShowFinalizeBid(false)} />

                {Object.entries(playedCardsByDirection).map(([dir, card]) => {
                    if (!card) return null;
                    const playRefObj = get(`play-${dir}`);
                    const containerRect = document.querySelector('.floating-card-layer')?.getBoundingClientRect();
                    if (!playRefObj?.current || !containerRect) return null;
                    const playRect = playRefObj.current.getBoundingClientRect();
                    const x = playRect.left + playRect.width / 2 - containerRect.left;
                    const y = playRect.top + playRect.height / 2 - containerRect.top;
                    return <img key={dir} src={`/static/img/deck/${card.cardImage}`} alt={`Played ${dir}`} className="card-img" style={{ position: 'absolute', left: x, top: y, transform: 'translate(-50%, -50%)', zIndex: 5 }} />;
                })}

                {playAnimations.map(anim => (
                    <PlayCardAnimation
                        key={anim.id}
                        card={anim.card}
                        fromRef={anim.fromRef}
                        toRef={anim.toRef}
                        onComplete={() => {
                            setPlayAnimations(prev => prev.filter(a => a.id !== anim.id));
                            setPlayedCardsByDirection(prev => ({ ...prev, [anim.direction]: anim.card }));
                        }}
                    />
                ))}

                {showAnimatedCards && animatedCards.map((card, i) => (
                    <AnimatedCard key={card.id} card={card} from={card.from} to={card.to} zIndex={i} />
                ))}
            </div>
        </div>
    );
}
