package src.main.game.phase;

import src.main.game.GameController;
import src.main.game.ProductionEngine;
import src.main.game.EventHandler;
import src.main.domain.PlayerState;

public class RollDice1 implements IGamePhase {
    private static final int BRIGAND_DIE_FACE = 1;

    private final ProductionEngine productionEngine;
    private final EventHandler eventHandler;

    public RollDice1(ProductionEngine productionEngine, EventHandler eventHandler) {
        if (productionEngine == null || eventHandler == null) {
            throw new IllegalArgumentException("Dependencies cannot be null");
        }
        this.productionEngine = productionEngine;
        this.eventHandler = eventHandler;
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        controller.notifyPhaseStart(getName());

        // Roll both dice
        int eventDieFace = controller.rollEventDie();
        int productionDieFace = controller.rollProductionDie();

        controller.notifyDiceRolled(eventDieFace, productionDieFace);

        // Handle in correct order based on event die
        if (eventDieFace == BRIGAND_DIE_FACE) {
            // Brigand first, then production
            eventHandler.handleEvent(controller, eventDieFace);
            productionEngine.produceResources(controller, productionDieFace);
        } else {
            // Production first, then event
            productionEngine.produceResources(controller, productionDieFace);
            eventHandler.handleEvent(controller, eventDieFace);
        }
    }

    @Override
    public boolean canEndTurnEarly() {
        return true; // Can end if player reaches 7+ VP
    }

    @Override
    public String getName() {
        return "Roll Dice Phase";
    }
}