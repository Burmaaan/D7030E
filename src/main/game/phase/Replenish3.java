package src.main.game.phase;

public class Replenish3 implements IGamePhase {
    @Override
    public void execute(GameController controller, PlayerState player) {
        controller.promptDrawCard(player);
    }

    @Override
    public boolean canEndTurnEarly() { return false; }
    @Override
    public String getName() { return "Replenish Phase"; }
}

