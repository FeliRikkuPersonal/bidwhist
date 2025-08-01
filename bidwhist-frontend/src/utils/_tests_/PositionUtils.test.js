// src/utils/__tests__/PositionUtils.test.js

import { describe, it, expect, vi } from 'vitest';
import { getPositionMap } from '../PositionUtils';

describe('getPositionMap', () => {
  const players = {
    P1: 'Alice',
    P2: 'Bob',
    P3: 'Carol',
    P4: 'Dave',
  };

  it('maps correctly when viewer is P1', () => {
    const result = getPositionMap(players, 'P1');
    expect(result).toEqual({
      P1: 'south',
      P2: 'west',
      P3: 'north',
      P4: 'east',
    });
  });

  it('maps correctly when viewer is P2', () => {
    const result = getPositionMap(players, 'P2');
    expect(result).toEqual({
      P2: 'south',
      P3: 'west',
      P4: 'north',
      P1: 'east',
    });
  });

  it('maps correctly when viewer is P3', () => {
    const result = getPositionMap(players, 'P3');
    expect(result).toEqual({
      P3: 'south',
      P4: 'west',
      P1: 'north',
      P2: 'east',
    });
  });

  it('maps correctly when viewer is P4', () => {
    const result = getPositionMap(players, 'P4');
    expect(result).toEqual({
      P4: 'south',
      P1: 'west',
      P2: 'north',
      P3: 'east',
    });
  });

  it('returns empty object and warns if viewer position not found', () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

    const result = getPositionMap(players, 'P9');
    expect(result).toEqual({});
    expect(warnSpy).toHaveBeenCalledWith('Viewer position P9 not found in backend positions');

    warnSpy.mockRestore();
  });
});
