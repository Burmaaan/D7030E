package game.phase;

import src.main.game.GameController;
import src.main.domain.PlayerState;
import src.main.game.phase.IGamePhase;

import java.util.Random;

public class RollDice1 implements IGamePhase {

    private final Random random = new Random();

    @Override
    public String getName() {
        return "Roll Dice Phase";
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        System.out.println("[Phase] Rolling dice...");

        int eventDie = rollDie();
        int productionDie = rollDie();

        System.out.println("Event die: " + eventDie + ", Production die: " + productionDie);

        // 1️⃣ Resolve Event first if brigand (1)
        if (eventDie == 1) {
            resolveEvent(eventDie, controller, player);
            handleProduction(productionDie, controller, player);
        } else {
            handleProduction(productionDie, controller, player);
            resolveEvent(eventDie, controller, player);
        }
    }

    @Override
    public boolean canEndTurnEarly() {
        return false;
    }

    private int rollDie() {
        return 1 + random.nextInt(6);
    }

    private void handleProduction(int productionDie, GameController controller, PlayerState player) {
        System.out.println("[Production] Each player receives resources for production value: " + productionDie);
        // TODO: Distribute resources according to productionDie and board layout
    }

    private void resolveEvent(int eventDie, GameController controller, PlayerState player) {
        System.out.println("[Event] Resolving event type: " + eventDie);
        switch (eventDie) {
            case 1 -> System.out.println("Brigand attack! Lose wool/gold if >7 resources.");
            case 2 -> System.out.println("Trade advantage event! Resource exchange triggered.");
            case 3 -> System.out.println("Celebration! Reward skill leader or all players.");
            case 4 -> System.out.println("Plentiful Harvest! Each player gains one resource of choice.");
            case 5, 6 -> System.out.println("Event Card! Draw and resolve event card.");
            default -> System.out.println("Invalid event roll.");
        }
        // TODO: Implement event handling with resource logic
    }
}
