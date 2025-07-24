// src/utils/__tests__/CardRefUtil.test.js

import { describe, it, expect } from 'vitest';
import { getCardPosition } from '../CardRefUtil';

describe('getCardPosition', () => {
  it('returns correct center coordinates when ref is valid', () => {
    const mockGet = (key) => ({
      current: {
        getBoundingClientRect: () => ({
          left: 100,
          top: 50,
          width: 80,
          height: 40,
        }),
      },
    });

    const result = getCardPosition(mockGet, 'play-south');
    expect(result).toEqual({ x: 140, y: 70 }); // 100 + 40, 50 + 20
  });

  it('returns null if ref is missing', () => {
    const mockGet = () => null;
    const result = getCardPosition(mockGet, 'play-north');
    expect(result).toBeNull();
  });

  it('returns null if ref.current is undefined', () => {
    const mockGet = () => ({ current: null });
    const result = getCardPosition(mockGet, 'play-west');
    expect(result).toBeNull();
  });
});
