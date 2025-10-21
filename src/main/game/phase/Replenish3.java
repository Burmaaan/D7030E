package src.main.game.phase;

import src.main.game.GameController;
import src.main.domain.PlayerState;

public class Replenish3 implements IGamePhase {
    private static final int BASE_HAND_SIZE = 3;

    @Override
    public void execute(GameController controller, PlayerState player) {
        controller.notifyPhaseStart(getName());

        // Check if player has "NO_REPLENISH_ONCE" flag (from Fraternal Feuds event)
        if (player.hasFlag("NO_REPLENISH_ONCE")) {
            player.removeFlag("NO_REPLENISH_ONCE");
            controller.notifyMessage(player,
                    "You cannot replenish your hand this turn (Fraternal Feuds).");
            return;
        }

        // Calculate target hand size: 3 + progress points
        int targetHandSize = BASE_HAND_SIZE + player.getProgressPoints();
        int currentHandSize = player.getHandSize();

        controller.notifyMessage(player,
                String.format("Target hand size: %d (current: %d)",
                        targetHandSize, currentHandSize));

        // Draw cards until hand is full
        while (currentHandSize < targetHandSize) {
            DrawStackChoice choice = controller.promptDrawStackChoice(player);

            if (choice == null || !controller.hasCardsInStack(choice.getStackNumber())) {
                // Try to find a non-empty stack
                choice = findNonEmptyStack(controller);
                if (choice == null) {
                    controller.notifyMessage(player, "All draw stacks are empty.");
                    break;
                }
            }

            try {
                controller.drawCardFromStack(player, choice.getStackNumber());
                currentHandSize++;
            } catch (Exception e) {
                controller.notifyError("Failed to draw card: " + e.getMessage());
                break;
            }
        }
    }

    private DrawStackChoice findNonEmptyStack(GameController controller) {
        for (int i = 1; i <= 4; i++) {
            if (controller.hasCardsInStack(i)) {
                return new DrawStackChoice(i);
            }
        }
        return null;
    }

    @Override
    public boolean canEndTurnEarly() {
        return false;
    }

    @Override
    public String getName() {
        return "Replenish Phase";
    }

    /**
     * Represents a player's choice of which draw stack to draw from
     */
    public static class DrawStackChoice {
        private final int stackNumber;

        public DrawStackChoice(int stackNumber) {
            if (stackNumber < 1 || stackNumber > 4) {
                throw new IllegalArgumentException("Stack number must be 1-4");
            }
            this.stackNumber = stackNumber;
        }

        public int getStackNumber() {
            return stackNumber;
        }
    }
}