package ru.nsu.tumilevich.client;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.nsu.tumilevich.server.GameServer;

public class MenuController {
    @FXML
    private TextField nameField;

    @FXML
    private void onStartGame() {
        GameClient client = GameClient.getInstance();
        if (client == null) {
            client = new GameClient();
        }
        client.setPlayerName(nameField.getText());
        client.startGame((Stage) nameField.getScene().getWindow());
    }

    @FXML
    private void onStartServer() {
        new Thread(() -> {
            GameServer server = new GameServer();
            server.start();
        }).start();
        System.out.println("Server started in background...");
    }
}
