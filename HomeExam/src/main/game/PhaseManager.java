package src.main.game;

import src.main.game.phase.*;
import src.main.domain.PlayerState;
import java.util.List;

public class PhaseManager {
    private final List<IGamePhase> phases;

    public PhaseManager(ProductionEngine production,
                        EventHandler event,
                        CardEffectHandler effects) {
        if (production == null || event == null || effects == null) {
            throw new IllegalArgumentException("All dependencies must be non-null");
        }

        this.phases = List.of(
                new RollDice1(production, event),
                new Action2(effects),
                new Replenish3(),
                new Exchange4()
        );
    }

    /**
     * Executes a complete turn for the given player
     *
     * @param controller The game controller
     * @param player The player whose turn it is
     */
    public void executeTurn(GameController controller, PlayerState player) {
        controller.notifyTurnStart(player);

        for (IGamePhase phase : phases) {
            try {
                phase.execute(controller, player);

                // Check win condition after each phase
                if (phase.canEndTurnEarly() && controller.checkWinCondition(player)) {
                    controller.notifyGameOver(player);
                    break;
                }
            } catch (Exception e) {
                controller.notifyError("Error in " + phase.getName() + ": " + e.getMessage());
                // Continue with next phase rather than crashing
            }
        }

        controller.notifyTurnEnd(player);
    }

    /**
     * Gets the list of phases in execution order
     */
    public List<IGamePhase> getPhases() {
        return List.copyOf(phases);
    }
}