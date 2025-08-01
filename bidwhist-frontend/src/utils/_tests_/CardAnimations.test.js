// src/utils/__tests__/CardAnimations.test.js

import { describe, it, expect, vi, beforeEach } from 'vitest';
import { getAnimationPath } from '../CardAnimations';
import * as RefContext from '../../context/RefContext';

describe('getAnimationPath', () => {
  let mockGetBoundingClientRect;

  beforeEach(() => {
    // Mock DOMRect-like objects
    mockGetBoundingClientRect = vi.fn();

    // Mock useZoneRefs to return refs with getBoundingClientRect()
    vi.spyOn(RefContext, 'useZoneRefs').mockReturnValue({
      get: (key) => {
        if (key === 'from') {
          return {
            current: {
              getBoundingClientRect: () => ({ left: 10, top: 20, width: 100, height: 50 }),
            },
          };
        }
        if (key === 'to') {
          return {
            current: {
              getBoundingClientRect: () => ({ left: 210, top: 120, width: 60, height: 40 }),
            },
          };
        }
        return null;
      },
    });
  });

  it('returns correct center coordinates for valid refs', () => {
    const result = getAnimationPath('from', 'to');

    expect(result).toEqual({
      from: { x: 10 + 100 / 2, y: 20 + 50 / 2 }, // x: 60, y: 45
      to: { x: 210 + 60 / 2, y: 120 + 40 / 2 }, // x: 240, y: 140
    });
  });

  it('returns null if from ref is missing', () => {
    RefContext.useZoneRefs.mockReturnValueOnce({
      get: (key) =>
        key === 'to'
          ? {
              current: {
                getBoundingClientRect: () => ({ left: 210, top: 120, width: 60, height: 40 }),
              },
            }
          : null,
    });

    const result = getAnimationPath('from', 'to');
    expect(result).toBeNull();
  });

  it('returns null if to ref is missing', () => {
    RefContext.useZoneRefs.mockReturnValueOnce({
      get: (key) =>
        key === 'from'
          ? {
              current: {
                getBoundingClientRect: () => ({ left: 10, top: 20, width: 100, height: 50 }),
              },
            }
          : null,
    });

    const result = getAnimationPath('from', 'to');
    expect(result).toBeNull();
  });
});
