package src.main;



import src.main.player.*;
import src.main.game.*;
import src.main.domain.card.factories.*;

import java.util.List;
import java.util.ArrayList;

public class RivalsForCatan {

    public static void main(String[] args) {
        // --- 1. Setup players (input handlers only) ---
        List<Player> players = new ArrayList<>();
        players.add(new LocalPlayer("Alice"));   // reads input from console
        players.add(new RemotePlayer("Bob"));    // reads input from network

        // --- 2. Setup deck using card factories ---
        AbstractCardFactory cardFactory = new DefaultCardFactory();
        DeckManager deckManager = new DeckManager(cardFactory);

        // --- 3. Setup game controller ---
        GameController controller = new StandardGameController(players, deckManager);

        // --- 4. Start the game ---
        controller.startGame();

        System.out.println("Game has ended!");
    }
}

