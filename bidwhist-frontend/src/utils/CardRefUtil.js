// src/utils/CardRefUtil.js
import { useZoneRefs } from '../context/RefContext';


export function getCardPosition(get, key) {
  const zoneRef = get(key);
  if (!zoneRef?.current) return null;

  const rect = zoneRef.current.getBoundingClientRect();
  return {
    x: rect.left + rect.width / 2,
    y: rect.top + rect.height / 2
  };
}


