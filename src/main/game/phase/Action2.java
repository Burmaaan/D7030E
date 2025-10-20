package src.main.game.phase;

public class Action2 implements IGamePhase {
    private final CardEffectHandler cardEffectHandler;

    public Action2(CardEffectHandler handler) {
        this.cardEffectHandler = handler;
    }

    @Override
    public void execute(GameController controller, PlayerState player) {
        boolean done = false;
        while (!done) {
            controller.displayHand(player);
            PlayerAction action = controller.askPlayerAction();

            switch (action) {
                case PLAY_CARD -> cardEffectHandler.playCard(player, controller.getGameState());
                case TRADE -> controller.trade(player);
                case END_ACTIONS -> done = true;
            }
        }
    }

    @Override
    public boolean canEndTurnEarly() { return false; }
    @Override
    public String getName() { return "Action Phase"; }
}

