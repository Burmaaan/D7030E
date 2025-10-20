package src.main.game.phase;

public class RollDice1 implements IGamePhase {
    private final ProductionEngine productionEngine;
    private final EventHandler eventHandler;

    public RollDice1(ProductionEngine productionEngine, EventHandler eventHandler) {
        this.productionEngine = productionEngine;
        this.eventHandler = eventHandler;
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        DiceResult eventResult = controller.rollEventDie();
        DiceResult productionResult = controller.rollProductionDie();

        if (eventResult == DiceResult.BRIGAND)
            eventHandler.handleBrigand(controller.getGameState());
        else {
            productionEngine.produceResources(controller.getGameState(), productionResult);
            eventHandler.handleEvent(controller.getGameState(), eventResult);
        }

        controller.askIfEndTurn(player);
    }

    @Override
    public boolean canEndTurnEarly() { return true; }

    @Override
    public String getName() { return "Roll Dice Phase"; }
}

