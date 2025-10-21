package src.main.game;

import src.main.domain.PlayerState;
import src.main.domain.card.Card;
import src.main.game.phase.PlayerAction;
import src.main.game.phase.Replenish3.DrawStackChoice;
import src.main.game.phase.Exchange4;
import src.main.game.phase.ExchangeMode;

/**
 * Contract for game orchestration and player interaction.
 * Separates game logic from UI/networking concerns.
 */
public interface GameController {

    // ========== Dice Rolling ==========
    int rollEventDie();
    int rollProductionDie();

    // ========== Notifications (Observer Pattern) ==========
    void notifyPhaseStart(String phaseName);
    void notifyTurnStart(PlayerState player);
    void notifyTurnEnd(PlayerState player);
    void notifyDiceRolled(int eventDie, int productionDie);
    void notifyMessage(PlayerState player, String message);
    void notifyError(String errorMessage);
    void notifyInvalidAction(String reason);
    void notifyGameOver(PlayerState winner);

    // ========== Display Methods ==========
    void displayPlayerBoard(PlayerState player);
    void displayPlayerHand(PlayerState player);
    void displayStackContents(PlayerState player, int stackNumber);

    // ========== Player Input Methods ==========
    PlayerAction promptPlayerAction(PlayerState player);
    DrawStackChoice promptDrawStackChoice(PlayerState player);
    ExchangeCardChoice promptCardToExchange(PlayerState player);
    ExchangeMode promptExchangeMode(PlayerState player, int searchCost);
    String promptResourceToDiscard(PlayerState player, int resourceNumber);
    String promptCardNameFromStack(PlayerState player);
    int promptStackToPlaceUnder(PlayerState player);
    boolean askYesNo(PlayerState player, String question);

    // ========== Game State Queries ==========
    boolean hasCardsInStack(int stackNumber);
    boolean checkWinCondition(PlayerState player);
    PlayerState getGameState();
    PlayerState getOpponent(PlayerState player);

    // ========== Game Actions ==========
    void drawCardFromStack(PlayerState player, int stackNumber);
    void removeCardFromHand(PlayerState player, int cardIndex);
    void placeCardUnderStack(Object card, int stackNumber);
    boolean takeCardFromStackByName(PlayerState player, int stackNumber, String cardName);
    void performTrade(PlayerState player);
    void buildCenterCard(PlayerState player);

    // ========== Turn Management ==========
    boolean shouldEndTurn(PlayerState player);
}