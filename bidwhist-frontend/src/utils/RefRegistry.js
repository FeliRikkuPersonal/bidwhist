// src/utils/refRegistry.js
let zoneRefs = new Map();

export function registerRef(key, refObj) {
  zoneRefs.set(key, refObj);
}

export function getRef(key) {
  return zoneRefs.get(key);
}
