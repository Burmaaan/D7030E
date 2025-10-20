package src.main.game.phase;

public class Exchange4 implements IGamePhase {
    @Override
    public void execute(GameController controller, PlayerState player) {
        if (controller.askIfExchangeCard(player)) {
            controller.performExchange(player);
        }
    }

    @Override
    public boolean canEndTurnEarly() { return false; }
    @Override
    public String getName() { return "Exchange Phase"; }
}
