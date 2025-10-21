package src.main.game.phase;

import src.main.game.GameController;
import src.main.domain.PlayerState;
import src.main.game.phase.ExchangeMode;

public class Exchange4 implements IGamePhase {
    private static final int BASE_HAND_LIMIT = 3;
    private static final int NORMAL_SEARCH_COST = 2;
    private static final int PARISH_SEARCH_COST = 1;

    @Override
    public void execute(GameController controller, PlayerState player) {
        controller.notifyPhaseStart(getName());

        // Calculate hand limit
        int handLimit = BASE_HAND_LIMIT + player.getProgressPoints();
        int currentHandSize = player.getHandSize();

        // Exchange only allowed if at/above limit
        if (currentHandSize < handLimit) {
            controller.notifyMessage(player,
                    String.format("Exchange skipped: hand size (%d) below limit (%d)",
                            currentHandSize, handLimit));
            return;
        }

        // Ask if player wants to exchange
        if (!controller.askYesNo(player, "Do you want to exchange a card?")) {
            return;
        }

        // Player chooses card to put under stack
        ExchangeCardChoice cardToExchange = controller.promptCardToExchange(player);
        if (cardToExchange == null) {
            controller.notifyMessage(player, "Exchange cancelled.");
            return;
        }

        // Player chooses which stack to put it under
        int stackNumber = controller.promptStackToPlaceUnder(player);
        if (stackNumber < 1 || stackNumber > 4) {
            controller.notifyMessage(player, "Invalid stack number. Exchange cancelled.");
            return;
        }

        // Remove card from hand and place under stack
        controller.removeCardFromHand(player, cardToExchange.getCardIndex());
        controller.placeCardUnderStack(cardToExchange.getCard(), stackNumber);

        // Determine search cost (Parish Hall reduces cost from 2 to 1)
        boolean hasParishHall = player.hasFlag("PARISH");
        int searchCost = hasParishHall ? PARISH_SEARCH_COST : NORMAL_SEARCH_COST;

        // Ask: Random draw or Search?
        ExchangeMode mode = controller.promptExchangeMode(player, searchCost);

        if (mode == ExchangeMode.RANDOM) {
            performRandomDraw(controller, player, stackNumber);
        } else if (mode == ExchangeMode.SEARCH) {
            performSearch(controller, player, stackNumber, searchCost);
        }
    }

    private void performRandomDraw(GameController controller, PlayerState player, int stackNumber) {
        if (!controller.hasCardsInStack(stackNumber)) {
            controller.notifyMessage(player, "That stack is empty.");
            return;
        }

        controller.drawCardFromStack(player, stackNumber);
        controller.notifyMessage(player, "Drew top card from stack " + stackNumber);
    }

    private void performSearch(GameController controller, PlayerState player,
                               int stackNumber, int searchCost) {
        // Check if player can afford search
        if (player.getTotalResources() < searchCost) {
            controller.notifyMessage(player,
                    String.format("Not enough resources to search (need %d)", searchCost));
            return;
        }

        // Pay search cost
        for (int i = 0; i < searchCost; i++) {
            String resourceType = controller.promptResourceToDiscard(player, i + 1);
            if (!player.removeResource(resourceType, 1)) {
                controller.notifyMessage(player, "Failed to pay search cost.");
                return;
            }
        }

        // Show stack contents and let player choose
        if (!controller.hasCardsInStack(stackNumber)) {
            controller.notifyMessage(player, "That stack is empty.");
            return;
        }

        controller.displayStackContents(player, stackNumber);
        String chosenCardName = controller.promptCardNameFromStack(player);

        if (controller.takeCardFromStackByName(player, stackNumber, chosenCardName)) {
            controller.notifyMessage(player, "Took " + chosenCardName + " from stack.");
        } else {
            controller.notifyMessage(player, "Card not found in stack.");
        }
    }

    @Override
    public boolean canEndTurnEarly() {
        return false;
    }

    @Override
    public String getName() {
        return "Exchange Phase";
    }


    /**
     * Represents a card chosen for exchange
     */
    public static class ExchangeCardChoice {
        private final Object card; // Generic card type
        private final int cardIndex;

        public ExchangeCardChoice(Object card, int cardIndex) {
            this.card = card;
            this.cardIndex = cardIndex;
        }

        public Object getCard() {
            return card;
        }

        public int getCardIndex() {
            return cardIndex;
        }
    }
}