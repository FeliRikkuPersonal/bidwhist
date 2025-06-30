import { useZoneRefs } from '../context/RefContext';

/**
 * Calculates an animation path between two registered refs.
 * 
 * @param {string} fromKey - e.g., 'Deck', 'PlayerZone-P1'
 * @param {string} toKey - e.g., 'CardPlayZone', 'PlayerZone-P2'
 * @returns {Object} from and to {x, y} points
 */
export function getAnimationPath(fromKey, toKey) {
  const { get } = useZoneRefs();

  const fromRect = get(fromKey)?.current?.getBoundingClientRect();
  const toRect = get(toKey)?.current?.getBoundingClientRect();

  if (!fromRect || !toRect) return null;

  return {
    from: {
      x: fromRect.left + fromRect.width / 2,
      y: fromRect.top + fromRect.height / 2,
    },
    to: {
      x: toRect.left + toRect.width / 2,
      y: toRect.top + toRect.height / 2,
    },
  };
}
