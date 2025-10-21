package src.main.game.phase;

import src.main.game.GameController;
import src.main.domain.PlayerState;
import src.main.game.phase.IGamePhase;

public class Exchange4 implements IGamePhase {

    @Override
    public String getName() {
        return "Exchange Phase";
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        System.out.println("[Phase] Exchange a card.");

        // TODO: Ask player which card to return, which stack to draw from, or pay resources to choose
        System.out.println("Player may return a card and draw a new one (stub).");
    }

    @Override
    public boolean canEndTurnEarly() {
        return true;
    }
}
