package src.main.game;
import src.main.game.phase.*;

import java.util.List;

public class PhaseManager {
    private final List<IGamePhase> phases;

    public PhaseManager(ProductionEngine production, EventHandler event, CardEffectHandler effects) {
        phases = List.of(
                new RollDice1(production, event),
                new Action2(effects),
                new Replenish3(),
                new Exchange4()
        );
    }

    public void executeTurn(GameController controller, PlayerState player) {
        for (IGamePhase phase : phases) {
            controller.notifyPhaseStart(phase.getName());
            phase.execute(controller, player);

            if (phase.canEndTurnEarly() && controller.shouldEndTurn(player))
                break;
        }
    }
}

