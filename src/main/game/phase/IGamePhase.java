package src.main.game.phase;

public interface IGamePhase {
    String getName();
    void execute(GameController controller, PlayerState player);
    boolean canEndTurnEarly();
}

