package src.main.game;

/**
 * Observer interface for game events.
 * Allows UI, logging, networking to react to game changes.
 */
public interface GameObserver {
    void onGameEvent(GameEvent event);
}