package src.main.game.phase;

import src.main.game.GameController;
import src.main.domain.PlayerState;

public interface IGamePhase {
    String getName();
    void execute(GameController controller, PlayerState player);
    boolean canEndTurnEarly();
}

