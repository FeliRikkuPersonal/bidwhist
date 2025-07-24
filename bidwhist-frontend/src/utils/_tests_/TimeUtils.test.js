// src/utils/__tests__/TimeUtils.test.js

import { describe, it, expect, vi } from 'vitest';
import { delay } from '../TimeUtils';

describe('delay', () => {
  it('resolves after specified milliseconds', async () => {
    const spy = vi.fn();

    const start = Date.now();
    await delay(50).then(spy);
    const elapsed = Date.now() - start;

    expect(spy).toHaveBeenCalled();
    expect(elapsed).toBeGreaterThanOrEqual(45); // allow for small timing wiggle room
  });

  it('resolves immediately for 0ms', async () => {
    const spy = vi.fn();

    const start = Date.now();
    await delay(0).then(spy);
    const elapsed = Date.now() - start;

    expect(spy).toHaveBeenCalled();
    expect(elapsed).toBeLessThan(20); // nearly immediate
  });
});
