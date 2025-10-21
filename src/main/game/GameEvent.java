package src.main.game;

/**
 * Immutable event object for observer pattern
 */
public class GameEvent {
    public enum Type {
        PHASE_START,
        TURN_START,
        TURN_END,
        DICE_ROLLED,
        EVENT_DIE_ROLLED,
        PRODUCTION_DIE_ROLLED,
        MESSAGE,
        ERROR,
        GAME_OVER
    }

    private final Type type;
    private final Object data;
    private final long timestamp;

    public GameEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Type getType() { return type; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("GameEvent{type=%s, data=%s, time=%d}", type, data, timestamp);
    }
}