package src.main.game;

import src.main.domain.PlayerState;
import src.main.domain.card.Card;
import src.main.game.phase.Action2.PlayerAction;
import src.main.game.phase.Replenish3.DrawStackChoice;
import src.main.game.phase.Exchange4.*;
import src.main.player.Player;
import java.util.*;

/**
 * Standard implementation of GameController for 2-player Rivals for Catan.
 * Orchestrates game flow and delegates to specialized handlers.
 *
 * SOLID Compliance:
 * - SRP: Only orchestrates, doesn't implement game rules
 * - OCP: Can be extended via composition
 * - LSP: Fully implements GameController contract
 * - ISP: Interface is focused and minimal
 * - DIP: Depends on abstractions (Player interface)
 */
public class StandardGameController implements GameController {

    private static final int WIN_CONDITION_VP = 7;
    private static final int MAX_STACK_NUMBER = 4;

    // Dependencies (injected)
    private final Player player1;
    private final Player player2;
    private final DeckManager deckManager;
    private final Random random;
    private final List<GameObserver> observers;

    // Current game state
    private PlayerState currentPlayer;
    private PlayerState waitingPlayer;
    private int turnNumber;
    private boolean gameOver;

    /**
     * Constructor with dependency injection
     */
    public StandardGameController(
            Player player1,
            Player player2,
            DeckManager deckManager,
            Random random) {

        validateDependencies(player1, player2, deckManager, random);

        this.player1 = player1;
        this.player2 = player2;
        this.deckManager = deckManager;
        this.random = random;
        this.observers = new ArrayList<>();
        this.turnNumber = 0;
        this.gameOver = false;
    }

    /**
     * Convenience constructor with default Random
     */
    public StandardGameController(Player player1, Player player2, DeckManager deckManager) {
        this(player1, player2, deckManager, new Random());
    }

    private void validateDependencies(Player p1, Player p2, DeckManager dm, Random rand) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Players cannot be null");
        }
        if (dm == null) {
            throw new IllegalArgumentException("DeckManager cannot be null");
        }
        if (rand == null) {
            throw new IllegalArgumentException("Random cannot be null");
        }
    }

    // ========== Observer Pattern for Notifications ==========

    public void addObserver(GameObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(GameEvent event) {
        for (GameObserver observer : observers) {
            try {
                observer.onGameEvent(event);
            } catch (Exception e) {
                // Log but don't crash on observer failure
                System.err.println("Observer error: " + e.getMessage());
            }
        }
    }

    // ========== Dice Rolling ==========

    @Override
    public int rollEventDie() {
        int result = 1 + random.nextInt(6);
        notifyObservers(new GameEvent(GameEvent.Type.EVENT_DIE_ROLLED, result));
        return result;
    }

    @Override
    public int rollProductionDie() {
        int result = 1 + random.nextInt(6);
        notifyObservers(new GameEvent(GameEvent.Type.PRODUCTION_DIE_ROLLED, result));
        return result;
    }

    // ========== Notifications ==========

    @Override
    public void notifyPhaseStart(String phaseName) {
        notifyObservers(new GameEvent(GameEvent.Type.PHASE_START, phaseName));
        getCurrentPlayer().sendMessage("=== " + phaseName + " ===");
    }

    @Override
    public void notifyTurnStart(PlayerState player) {
        turnNumber++;
        currentPlayer = player;
        waitingPlayer = getOpponent(player);

        notifyObservers(new GameEvent(GameEvent.Type.TURN_START, player));
        player.sendMessage("\n========== TURN " + turnNumber + " ==========");
        player.sendMessage("Your turn begins.");
    }

    @Override
    public void notifyTurnEnd(PlayerState player) {
        notifyObservers(new GameEvent(GameEvent.Type.TURN_END, player));
        player.sendMessage("Your turn ends.\n");
    }

    @Override
    public void notifyDiceRolled(int eventDie, int productionDie) {
        String message = String.format("Dice: Event=%d, Production=%d", eventDie, productionDie);
        getCurrentPlayer().sendMessage(message);
        notifyObservers(new GameEvent(GameEvent.Type.DICE_ROLLED, message));
    }

    @Override
    public void notifyMessage(PlayerState player, String message) {
        player.sendMessage(message);
        notifyObservers(new GameEvent(GameEvent.Type.MESSAGE, message));
    }

    @Override
    public void notifyError(String errorMessage) {
        getCurrentPlayer().sendMessage("ERROR: " + errorMessage);
        notifyObservers(new GameEvent(GameEvent.Type.ERROR, errorMessage));
    }

    @Override
    public void notifyInvalidAction(String reason) {
        getCurrentPlayer().sendMessage("Invalid action: " + reason);
    }

    @Override
    public void notifyGameOver(PlayerState winner) {
        gameOver = true;
        String message = winner.getName() + " wins with " +
                winner.getFinalScore(getOpponent(winner)) + " victory points!";

        notifyObservers(new GameEvent(GameEvent.Type.GAME_OVER, winner));
        player1.sendMessage("\n" + message);
        player2.sendMessage("\n" + message);
    }

    // ========== Display Methods ==========

    @Override
    public void displayPlayerBoard(PlayerState player) {
        player.sendMessage(player.getBoardRepresentation());
    }

    @Override
    public void displayPlayerHand(PlayerState player) {
        player.sendMessage(player.getHandRepresentation());
    }

    @Override
    public void displayStackContents(PlayerState player, int stackNumber) {
        validateStackNumber(stackNumber);
        List<Card> cards = deckManager.getStackContents(stackNumber);

        StringBuilder sb = new StringBuilder("Stack " + stackNumber + " contents:\n");
        for (int i = 0; i < cards.size(); i++) {
            sb.append("  [").append(i).append("] ").append(cards.get(i).getName()).append("\n");
        }
        player.sendMessage(sb.toString());
    }

    // ========== Player Input Methods ==========

    @Override
    public PlayerAction promptPlayerAction(PlayerState player) {
        player.sendMessage("\nAvailable actions:");
        player.sendMessage("  1. PLAY_CARD - Play a card from your hand");
        player.sendMessage("  2. TRADE - Trade resources");
        player.sendMessage("  3. BUILD_CENTER - Build Road/Settlement/City");
        player.sendMessage("  4. END_ACTIONS - End action phase");
        player.sendMessage("Enter choice (1-4):");

        String input = player.receiveMessage();
        return parsePlayerAction(input);
    }

    private PlayerAction parsePlayerAction(String input) {
        try {
            int choice = Integer.parseInt(input.trim());
            switch (choice) {
                case 1: return PlayerAction.PLAY_CARD;
                case 2: return PlayerAction.TRADE;
                case 3: return PlayerAction.BUILD_CENTER;
                case 4: return PlayerAction.END_ACTIONS;
                default: return PlayerAction.END_ACTIONS;
            }
        } catch (NumberFormatException e) {
            return PlayerAction.END_ACTIONS;
        }
    }

    @Override
    public DrawStackChoice promptDrawStackChoice(PlayerState player) {
        player.sendMessage("Choose draw stack (1-4):");
        String input = player.receiveMessage();

        try {
            int stackNum = Integer.parseInt(input.trim());
            return new DrawStackChoice(stackNum);
        } catch (Exception e) {
            return new DrawStackChoice(1); // Default to stack 1
        }
    }

    @Override
    public ExchangeCardChoice promptCardToExchange(PlayerState player) {
        player.sendMessage("Enter card index to exchange:");
        String input = player.receiveMessage();

        try {
            int index = Integer.parseInt(input.trim());
            if (index >= 0 && index < player.getHandSize()) {
                Card card = player.getCardFromHand(index);
                return new ExchangeCardChoice(card, index);
            }
        } catch (Exception e) {
            // Invalid input
        }
        return null;
    }

    @Override
    public ExchangeMode promptExchangeMode(PlayerState player, int searchCost) {
        player.sendMessage("Exchange mode:");
        player.sendMessage("  1. RANDOM - Draw random card");
        player.sendMessage("  2. SEARCH - Choose specific card (costs " + searchCost + " resources)");
        player.sendMessage("Enter choice (1-2):");

        String input = player.receiveMessage();
        try {
            int choice = Integer.parseInt(input.trim());
            return (choice == 2) ? ExchangeMode.SEARCH : ExchangeMode.RANDOM;
        } catch (Exception e) {
            return ExchangeMode.RANDOM;
        }
    }

    @Override
    public String promptResourceToDiscard(PlayerState player, int resourceNumber) {
        player.sendMessage("Discard resource #" + resourceNumber + " (Brick/Grain/Lumber/Wool/Ore/Gold):");
        return player.receiveMessage().trim();
    }

    @Override
    public String promptCardNameFromStack(PlayerState player) {
        player.sendMessage("Enter exact card name:");
        return player.receiveMessage().trim();
    }

    @Override
    public int promptStackToPlaceUnder(PlayerState player) {
        player.sendMessage("Choose stack to place card under (1-4):");
        String input = player.receiveMessage();

        try {
            return Integer.parseInt(input.trim());
        } catch (Exception e) {
            return 1; // Default
        }
    }

    @Override
    public boolean askYesNo(PlayerState player, String question) {
        player.sendMessage(question + " (Y/N):");
        String input = player.receiveMessage();
        return input != null && input.trim().toUpperCase().startsWith("Y");
    }

    // ========== Game State Queries ==========

    @Override
    public boolean hasCardsInStack(int stackNumber) {
        validateStackNumber(stackNumber);
        return deckManager.hasCards(stackNumber);
    }

    @Override
    public boolean checkWinCondition(PlayerState player) {
        int score = player.getFinalScore(getOpponent(player));
        return score >= WIN_CONDITION_VP;
    }

    @Override
    public PlayerState getGameState() {
        return currentPlayer;
    }

    @Override
    public PlayerState getOpponent(PlayerState player) {
        return (player == player1.getState()) ? player2.getState() : player1.getState();
    }

    // ========== Game Actions ==========

    @Override
    public void drawCardFromStack(PlayerState player, int stackNumber) {
        validateStackNumber(stackNumber);

        Card card = deckManager.drawFromStack(stackNumber);
        if (card != null) {
            player.addCardToHand(card);
            notifyMessage(player, "Drew: " + card.getName());
        } else {
            notifyError("Stack " + stackNumber + " is empty");
        }
    }

    @Override
    public void removeCardFromHand(PlayerState player, int cardIndex) {
        if (cardIndex < 0 || cardIndex >= player.getHandSize()) {
            throw new IllegalArgumentException("Invalid card index: " + cardIndex);
        }
        player.removeCardFromHand(cardIndex);
    }

    @Override
    public void placeCardUnderStack(Object card, int stackNumber) {
        validateStackNumber(stackNumber);
        if (card instanceof Card) {
            deckManager.placeUnderStack((Card) card, stackNumber);
        }
    }

    @Override
    public boolean takeCardFromStackByName(PlayerState player, int stackNumber, String cardName) {
        validateStackNumber(stackNumber);

        Card card = deckManager.findAndRemoveCardByName(stackNumber, cardName);
        if (card != null) {
            player.addCardToHand(card);
            return true;
        }
        return false;
    }

    @Override
    public void performTrade(PlayerState player) {
        notifyMessage(player, "Trade functionality - implement based on trade rules");
        // Delegate to TradeHandler in full implementation
    }

    @Override
    public void buildCenterCard(PlayerState player) {
        notifyMessage(player, "Build center card - implement based on building rules");
        // Delegate to BuildHandler in full implementation
    }

    @Override
    public boolean shouldEndTurn(PlayerState player) {
        return checkWinCondition(player) || gameOver;
    }

    // ========== Helper Methods ==========

    private Player getCurrentPlayer() {
        return (currentPlayer == player1.getState()) ? player1 : player2;
    }

    private void validateStackNumber(int stackNumber) {
        if (stackNumber < 1 || stackNumber > MAX_STACK_NUMBER) {
            throw new IllegalArgumentException(
                    "Stack number must be 1-" + MAX_STACK_NUMBER + ", got: " + stackNumber);
        }
    }

    // ========== Getters ==========

    public int getTurnNumber() {
        return turnNumber;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}