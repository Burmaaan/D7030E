package main;

import src.main.player.*;
import src.main.game.*;
import src.main.domain.card.factories.*;
import src.main.domain.card.*;
import src.main.validation.PlacementValidator;

import java.util.List;
import java.util.ArrayList;

public class RivalsForCatan {

    public static void main(String[] args) {
        // --- 1. Setup players ---
        List<Player> players = new ArrayList<>();
        players.add(new LocalPlayer("Alice"));
        players.add(new RemotePlayer("Bob"));
        // add more players as needed

        // --- 2. Setup card factories and deck ---
        AbstractCardFactory cardFactory = new DefaultCardFactory();
        DeckManager deckManager = new DeckManager(cardFactory);

        // --- 3. Setup game controller ---
        GameController controller = new StandardGameController(players, deckManager);

        // --- 4. Setup validation and production engine ---
        PlacementValidator placementValidator = new PlacementValidator();
        ProductionEngine productionEngine = new ProductionEngine();

        // --- 5. Main game loop ---
        controller.startGame(placementValidator, productionEngine);

        System.out.println("Game has ended!");
    }
}
