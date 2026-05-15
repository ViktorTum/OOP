package ru.nsu.tumilevich;

import ru.nsu.tumilevich.client.GameClient;
import ru.nsu.tumilevich.server.GameServer;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            GameServer.main(args);
        } else {
            GameClient.main(args);
        }
    }
}
