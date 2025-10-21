package src.main.game.observer;

import src.main.game.GameEvent;

/**
 * Observer interface for game events.
 * Allows UI, logging, networking to react to game changes.
 */
public interface IGameObserver {
    void onGameEvent(GameEvent event);
}