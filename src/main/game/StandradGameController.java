package src.main.game;

import src.main.domain.PlayerState;
import src.main.game.phase.*;

import java.util.List;

public class StandardGameController implements GameController {

    private final List<PlayerState> players;
    private final DeckManager deckManager;
    private final IGamePhase[] phases;

    public StandardGameController(List<PlayerState> players, DeckManager deckManager) {
        this.players = players;
        this.deckManager = deckManager;

        // Initialize phases in order
        this.phases = new IGamePhase[]{
                new RollDice1(),
                new Action2(),
                new Replenish3(),
                new Exchange4()
        };
    }

    @Override
    public void startGame() {
        System.out.println("Starting Rivals for Catan!");

        boolean gameEnded = false;

        // Main game loop
        while (!gameEnded) {
            for (PlayerState player : players) {
                System.out.println("\n--- Player Turn ---");

                // Execute all phases
                for (IGamePhase phase : phases) {
                    phase.execute(this, player);
                    if (phase.canEndTurnEarly()) {
                        System.out.println("Phase ended early: " + phase.getName());
                        break;
                    }
                }

                // Check for winning condition (stub)
                if (checkWinCondition(player)) {
                    System.out.println("Player " + player + " wins the game!");
                    gameEnded = true;
                    break;
                }
            }
        }
    }

    private boolean checkWinCondition(PlayerState player) {
        // TODO: implement actual winning logic
        return false; // stub: always false for now
    }

    public DeckManager getDeckManager() {
        return deckManager;
    }

    // TODO: add other helpers as needed (board state, production engine, etc.)
}
