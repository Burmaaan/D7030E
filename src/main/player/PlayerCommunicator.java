package src.main.player;

/**
 * Strategy interface for player communication.
 * Allows different implementations (console, network, AI).
 */
public interface PlayerCommunicator {
    void send(String message);
    String receive();
}