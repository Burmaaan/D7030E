package src.main.game.phase;

import src.main.game.GameController;
import src.main.domain.PlayerState;
import src.main.game.phase.IGamePhase;

public class Action2 implements IGamePhase {

    @Override
    public String getName() {
        return "Action Phase";
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        System.out.println("[Phase] Action Phase - play cards, trade, build...");

        // TODO: Implement card playing, trading, and building logic
        // Currently just simulates a player taking an action
        System.out.println("Player performs basic actions (stub).");
    }

    @Override
    public boolean canEndTurnEarly() {
        return true;
    }
}
