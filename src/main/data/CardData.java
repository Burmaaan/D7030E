package src.main.data;


public class CardData {
    public final String name;
    public final String germanName;
    public final String type;
    public final String theme;
    public final String placement;
    public final String oneOf;
    public final String cost;
    public final String requires;
    public final String cardText;
    public final String protectionOrRemoval;
    public final String victoryPoints;
    public final String CP;
    public final String SP;
    public final String FP;
    public final String PP;
    public final String LP;
    public final String KP;
    public final int number;
    public final int sunSymbol;

    public CardData(String name, String theme, String type,
                    String germanName, String placement, String oneOf,
                    String cost, String victoryPoints, String CP,
                    String SP, String FP, String PP, String LP,
                    String KP, String requires, String cardText,
                    String protectionOrRemoval, int number, int sunSymbol) {
        this.name = name;
        this.theme = theme;
        this.type = type;
        this.germanName = germanName;
        this.placement = placement;
        this.oneOf = oneOf;
        this.cost = cost;
        this.victoryPoints = victoryPoints;
        this.CP = CP;
        this.SP = SP;
        this.FP = FP;
        this.PP = PP;
        this.LP = LP;
        this.KP = KP;
        this.requires = requires;
        this.cardText = cardText;
        this.protectionOrRemoval = protectionOrRemoval;
        this.number = number;
        this.sunSymbol = sunSymbol;
    }

    @Override
    public String toString() {
        return name;
    }
}