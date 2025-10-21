package src.main.game.phase;

import src.main.game.GameController;
import src.main.domain.PlayerState;
import src.main.game.phase.IGamePhase;

public class Replenish3 implements IGamePhase {

    @Override
    public String getName() {
        return "Replenish Phase";
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        System.out.println("[Phase] Replenish hand.");

        // TODO: Ask which deck to draw from, then draw 1 card
        System.out.println("Player chooses which deck to draw from (stub).");
    }

    @Override
    public boolean canEndTurnEarly() {
        return false;
    }
}
