// src/utils/CardRefUtil.js
import { useZoneRefs } from '../context/RefContext';

export function useCardPosition(key) {
  const { get } = useZoneRefs();
  const zoneRef = get(key);
  return zoneRef?.current?.getBoundingClientRect();
}

