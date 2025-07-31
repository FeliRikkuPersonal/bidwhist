// src/utils/__tests__/handleQuit.test.js

import { vi, describe, it, expect } from 'vitest';
import handleQuit from '../handleQuit';
import { clearAllGameData } from '../ClearData';

vi.mock('../ClearData', () => ({
  clearAllGameData: vi.fn(() => Promise.resolve()),
}));

describe('handleQuit', () => {
  const originalReload = window.location.reload;
  const originalHref = window.location.href;

  beforeEach(() => {
    // Mock global confirm
    vi.stubGlobal('confirm', vi.fn(() => true));
    // Mock reload and href
    delete window.location;
    window.location = { reload: vi.fn(), href: '' };
    vi.resetAllMocks();
  });

  afterAll(() => {
    window.location.reload = originalReload;
    window.location.href = originalHref;
  });

  it('calls API, clears data, and redirects if confirmed', async () => {
    const fakeFetch = vi.fn(() => Promise.resolve({ ok: true }));
    vi.stubGlobal('fetch', fakeFetch);

    await handleQuit({
      viewerPosition: 'P1',
      gameId: 'abc123',
      savedMode: 'single-player',
      API: 'http://localhost:3000',
      clearUIContext: vi.fn(),
      clearGameStateContext: vi.fn(),
    });

    expect(fetch).toHaveBeenCalledWith(
      'http://localhost:3000/game/quit',
      expect.objectContaining({
        method: 'POST',
        body: JSON.stringify({
          player: 'P1',
          gameId: 'abc123',
          mode: 'single-player',
        }),
      })
    );

    expect(clearAllGameData).toHaveBeenCalled();
    expect(window.location.reload).toHaveBeenCalled();
    expect(window.location.href).toBe('/');
  });

  it('does nothing if user cancels confirm dialog', async () => {
    confirm.mockReturnValueOnce(false);
    await handleQuit({});

    expect(fetch).not.toHaveBeenCalled();
    expect(clearAllGameData).not.toHaveBeenCalled();
  });
});
