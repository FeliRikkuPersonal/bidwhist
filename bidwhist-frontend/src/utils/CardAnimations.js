// src/utils/CardAnimations.js

import { useZoneRefs } from '../context/RefContext';

/**
 * Computes the start and end center positions of a card animation path.
 *
 * @param {string} fromKey - The ref key for the source zone
 * @param {string} toKey - The ref key for the target zone
 * @returns {{from: {x: number, y: number}, to: {x: number, y: number}} | null}
 *          Coordinates for the animation path or null if either ref is missing
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
