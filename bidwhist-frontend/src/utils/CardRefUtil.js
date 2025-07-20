// src/utils/CardRefUtil.js

import { useZoneRefs } from '../context/RefContext';

/**
 * Computes the screen-center position of a card zone using a ref getter.
 *
 * @param {Function} get - A function that retrieves a ref by key
 * @param {string} key - The key representing the ref zone to locate
 * @returns {{x: number, y: number} | null} The center coordinates of the ref zone, or null if not available
 */
export function getCardPosition(get, key) {
  const zoneRef = get(key);
  if (!zoneRef?.current) return null;

  const rect = zoneRef.current.getBoundingClientRect();
  return {
    x: rect.left + rect.width / 2,
    y: rect.top + rect.height / 2,
  };
}
