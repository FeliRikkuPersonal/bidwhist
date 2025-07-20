// src/utils/TimeUtils.js

/**
 * Delays execution for a given number of milliseconds.
 *
 * @param {number} ms - The number of milliseconds to delay
 * @returns {Promise<void>} A promise that resolves after the delay
 */
export function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}
