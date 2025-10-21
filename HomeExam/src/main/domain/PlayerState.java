package src.main.domain;

import src.main.domain.card.Card;
import src.main.player.PlayerCommunicator;

import java.util.*;

/**
 * Represents the complete state of a player.
 * Pure domain entity - NO game logic, only state and queries.
 *
 * SOLID Compliance:
 * - SRP: Only manages player state data
 * - OCP: Can be extended with new state fields
 * - LSP: Can be subclassed if needed
 * - ISP: Provides focused query/command methods
 * - DIP: Depends only on Card (domain entity)
 *
 * Booch Metrics:
 * - High cohesion: All methods relate to player state
 * - Low coupling: Only depends on Card
 * - Complete: All necessary player state represented
 * - Primitive: Methods are atomic state operations
 */
public class PlayerState {

    // ========== Identity ==========
    private final String playerName;
    private final int playerId;

    // ========== Victory Conditions ==========
    private int victoryPoints;
    private int progressPoints;
    private int skillPoints;
    private int commercePoints;
    private int strengthPoints;

    // ========== Resources (per-region storage) ==========
    private final Board board;

    // ========== Cards ==========
    private final List<Card> hand;
    private final int maxHandSize;

    // ========== Flags (for special abilities/states) ==========
    private final Set<String> flags;

    // ========== Communication Interface ==========
    private transient PlayerCommunicator communicator;

    /**
     * Constructor for new player
     */
    public PlayerState(String playerName, int playerId) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name cannot be null or empty");
        }

        this.playerName = playerName;
        this.playerId = playerId;

        // Initialize victory conditions
        this.victoryPoints = 0;
        this.progressPoints = 0;
        this.skillPoints = 0;
        this.commercePoints = 0;
        this.strengthPoints = 0;

        // Initialize collections
        this.board = new Board();
        this.hand = new ArrayList<>();
        this.maxHandSize = 10; // Base limit, modified by progress points
        this.flags = new HashSet<>();
    }

    // ========== Identity Queries ==========

    public String getName() {
        return playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    // ========== Victory Point Queries ==========

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public int getProgressPoints() {
        return progressPoints;
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    public int getCommercePoints() {
        return commercePoints;
    }

    public int getStrengthPoints() {
        return strengthPoints;
    }

    /**
     * Calculates final score including advantage tokens
     * Trade advantage (+3 CP over opponent) = +1 VP
     * Strength advantage (+3 FP over opponent) = +1 VP
     */
    public int getFinalScore(PlayerState opponent) {
        int score = victoryPoints;

        if (opponent != null) {
            // Trade advantage
            if (this.commercePoints - opponent.commercePoints >= 3) {
                score += 1;
            }
            // Strength advantage
            if (this.strengthPoints - opponent.strengthPoints >= 3) {
                score += 1;
            }
        }

        return score;
    }

    public boolean hasTradeAdvantageOver(PlayerState opponent) {
        return opponent != null &&
                (this.commercePoints - opponent.commercePoints) >= 3;
    }

    public boolean hasStrengthAdvantageOver(PlayerState opponent) {
        return opponent != null &&
                (this.strengthPoints - opponent.strengthPoints) >= 3;
    }

    // ========== Victory Point Mutations (Command Pattern) ==========

    public void addVictoryPoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add negative victory points");
        }
        this.victoryPoints += amount;
    }

    public void addProgressPoints(int amount) {
        this.progressPoints = Math.max(0, this.progressPoints + amount);
    }

    public void addSkillPoints(int amount) {
        this.skillPoints = Math.max(0, this.skillPoints + amount);
    }

    public void addCommercePoints(int amount) {
        this.commercePoints = Math.max(0, this.commercePoints + amount);
    }

    public void addStrengthPoints(int amount) {
        this.strengthPoints = Math.max(0, this.strengthPoints + amount);
    }

    // ========== Resource Queries ==========

    /**
     * Gets total resources across all regions
     */
    public int getTotalResources() {
        return board.getTotalResources();
    }

    /**
     * Gets count of a specific resource type
     */
    public int getResourceCount(String resourceType) {
        return board.getResourceCount(resourceType);
    }

    /**
     * Checks if player has enough of a resource
     */
    public boolean hasResources(String resourceType, int amount) {
        return getResourceCount(resourceType) >= amount;
    }

    // ========== Resource Mutations ==========

    /**
     * Adds one resource of the specified type
     * Delegates to board for region-based storage
     */
    public void gainResource(String resourceType) {
        board.addResource(resourceType, 1);
    }

    /**
     * Removes resources of a type
     * @return true if successful, false if insufficient resources
     */
    public boolean removeResource(String resourceType, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot remove negative amount");
        }
        if (!hasResources(resourceType, amount)) {
            return false;
        }

        board.removeResource(resourceType, amount);
        return true;
    }

    /**
     * Sets exact count of a resource (for events/effects)
     */
    public void setResourceCount(String resourceType, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Resource count cannot be negative");
        }
        board.setResourceCount(resourceType, count);
    }

    // ========== Hand Queries ==========

    public int getHandSize() {
        return hand.size();
    }

    public boolean isHandFull() {
        int limit = 3 + progressPoints; // Base 3 + PP bonus
        return hand.size() >= limit;
    }

    public List<Card> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public Card getCardFromHand(int index) {
        if (index < 0 || index >= hand.size()) {
            throw new IndexOutOfBoundsException("Invalid hand index: " + index);
        }
        return hand.get(index);
    }

    public boolean hasCardInHand(String cardName) {
        return hand.stream()
                .anyMatch(card -> card.getName().equalsIgnoreCase(cardName));
    }

    // ========== Hand Mutations ==========

    public void addCardToHand(Card card) {
        if (card == null) {
            throw new IllegalArgumentException("Cannot add null card to hand");
        }
        hand.add(card);
    }

    public Card removeCardFromHand(int index) {
        if (index < 0 || index >= hand.size()) {
            throw new IndexOutOfBoundsException("Invalid hand index: " + index);
        }
        return hand.remove(index);
    }

    public Card removeCardFromHandByName(String cardName) {
        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            if (card.getName().equalsIgnoreCase(cardName)) {
                return hand.remove(i);
            }
        }
        return null;
    }

    // ========== Board Queries ==========

    public Board getBoard() {
        return board;
    }

    public Card getCardAt(int row, int col) {
        return board.getCardAt(row, col);
    }

    public boolean hasBuilding(String buildingName) {
        return board.hasCard(buildingName);
    }

    // ========== Board Mutations ==========

    public void placeCard(int row, int col, Card card) {
        board.placeCard(row, col, card);
    }

    public Card removeCard(int row, int col) {
        return board.removeCard(row, col);
    }

    // ========== Flag Queries ==========

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public Set<String> getFlags() {
        return Collections.unmodifiableSet(flags);
    }

    // ========== Flag Mutations ==========

    public void addFlag(String flag) {
        if (flag != null && !flag.isBlank()) {
            flags.add(flag);
        }
    }

    public boolean removeFlag(String flag) {
        return flags.remove(flag);
    }

    public void clearFlags() {
        flags.clear();
    }

    // ========== Communication (Strategy Pattern) ==========

    public void setCommunicator(PlayerCommunicator communicator) {
        this.communicator = communicator;
    }

    public void sendMessage(String message) {
        if (communicator != null) {
            communicator.send(message);
        } else {
            System.out.println("[" + playerName + "] " + message);
        }
    }

    public String receiveMessage() {
        if (communicator != null) {
            return communicator.receive();
        } else {
            Scanner scanner = new Scanner(System.in);
            return scanner.nextLine();
        }
    }

    // ========== Display Methods ==========

    public String getBoardRepresentation() {
        return board.toString();
    }

    public String getHandRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hand (").append(hand.size()).append("):\n");

        for (int i = 0; i < hand.size(); i++) {
            Card card = hand.get(i);
            sb.append("  [").append(i).append("] ")
                    .append(card.getName())
                    .append(" {Cost: ").append(card.getCost()).append("}")
                    .append("\n");
        }

        return sb.toString();
    }

    public String getPointsSummary() {
        return String.format("VP=%d CP=%d SP=%d FP=%d PP=%d",
                victoryPoints, commercePoints, skillPoints, strengthPoints, progressPoints);
    }

    @Override
    public String toString() {
        return String.format("Player[%s, %s, Hand:%d, Resources:%d]",
                playerName, getPointsSummary(), hand.size(), getTotalResources());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PlayerState)) return false;
        PlayerState other = (PlayerState) obj;
        return this.playerId == other.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }
}