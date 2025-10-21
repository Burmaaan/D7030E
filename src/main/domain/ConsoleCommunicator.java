package src.main.domain;

import java.util.Scanner;

public class ConsoleCommunicator implements PlayerCommunicator {
    private final Scanner scanner;
    private final String playerName;

    public ConsoleCommunicator(String playerName) {
        this.scanner = new Scanner(System.in);
        this.playerName = playerName;
    }

    @Override
    public void send(String message) {
        System.out.println("[" + playerName + "] " + message);
    }

    @Override
    public String receive() {
        System.out.print("> ");
        return scanner.nextLine();
    }
}