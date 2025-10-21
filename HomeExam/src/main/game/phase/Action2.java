package src.main.game.phase;

import src.main.game.GameController;
import src.main.game.CardEffectHandler;
import src.main.domain.PlayerState;

public class Action2 implements IGamePhase {
    private final CardEffectHandler cardEffectHandler;

    public Action2(CardEffectHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("CardEffectHandler cannot be null");
        }
        this.cardEffectHandler = handler;
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        controller.notifyPhaseStart(getName());

        boolean continueActions = true;
        while (continueActions) {
            // Display current state
            controller.displayPlayerBoard(player);
            controller.displayPlayerHand(player);

            // Ask for action
            PlayerAction action = controller.promptPlayerAction(player);

            // Execute action
            switch (action) {
                case PLAY_CARD:
                    handlePlayCard(controller, player);
                    break;

                case TRADE:
                    handleTrade(controller, player);
                    break;

                case BUILD_CENTER:
                    handleBuildCenter(controller, player);
                    break;

                case END_ACTIONS:
                    continueActions = false;
                    break;

                default:
                    controller.notifyInvalidAction("Unknown action: " + action);
            }
        }
    }

    private void handlePlayCard(GameController controller, PlayerState player) {
        try {
            cardEffectHandler.playCardFromHand(controller, player);
        } catch (Exception e) {
            controller.notifyError("Failed to play card: " + e.getMessage());
        }
    }

    private void handleTrade(GameController controller, PlayerState player) {
        try {
            controller.performTrade(player);
        } catch (Exception e) {
            controller.notifyError("Trade failed: " + e.getMessage());
        }
    }

    private void handleBuildCenter(GameController controller, PlayerState player) {
        try {
            controller.buildCenterCard(player);
        } catch (Exception e) {
            controller.notifyError("Build failed: " + e.getMessage());
        }
    }

    @Override
    public boolean canEndTurnEarly() {
        return false; // Actions cannot end turn early
    }

    @Override
    public String getName() {
        return "Action Phase";
    }

    /**
     * Player actions available during this phase
     */
    public enum PlayerAction {
        PLAY_CARD,
        TRADE,
        BUILD_CENTER,
        END_ACTIONS
    }
}