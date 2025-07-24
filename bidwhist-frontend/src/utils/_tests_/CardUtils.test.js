// src/utils/__tests__/CardUtils.test.js

import { describe, it, expect, vi } from 'vitest';
import { getCardImage } from '../CardUtils';

describe('getCardImage', () => {
  it('returns the correct image path when card is visible to all', () => {
    const card = {
      visibility: 'VISIBLE_TO_ALL',
      cardImage: '5H.png',
    };

    const result = getCardImage(card);
    expect(result).toBe('/static/img/deck/5H.png');
  });

  it('returns the back of the card when visibility is not VISIBLE_TO_ALL', () => {
    const card = {
      visibility: 'HIDDEN',
      cardImage: '5H.png',
    };

    const result = getCardImage(card);
    expect(result).toBe('/static/img/deck/Deck_Back.png');
  });

  it('returns the back of the card and warns if card is null', () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

    const result = getCardImage(null);
    expect(result).toBe('/static/img/deck/Deck_Back.png');
    expect(warnSpy).toHaveBeenCalledWith('getCardImage called with null/undefined card');

    warnSpy.mockRestore();
  });

  it('returns the back of the card and warns if card is undefined', () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

    const result = getCardImage(undefined);
    expect(result).toBe('/static/img/deck/Deck_Back.png');
    expect(warnSpy).toHaveBeenCalledWith('getCardImage called with null/undefined card');

    warnSpy.mockRestore();
  });
});
