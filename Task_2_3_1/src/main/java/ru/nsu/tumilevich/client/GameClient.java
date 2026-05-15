package ru.nsu.tumilevich.client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.nsu.tumilevich.model.*;
import ru.nsu.tumilevich.network.Message;
import ru.nsu.tumilevich.network.PlayerInput;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

public class GameClient extends Application {
    private Image shipImage;
    private GameState gameState = new GameState();
    private final PlayerInput currentInput = new PlayerInput();
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private ObjectOutputStream out;
    private double mouseX, mouseY;
    private String playerName = "Player";

    public void setPlayerName(String name) { this.playerName = name; }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if (out == null && !primaryStage.getTitle().equals("Space Shooter Multiplayer")) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/nsu/tumilevich/menu.fxml"));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("Space Shooter Menu");
            primaryStage.show();
            return;
        }

        shipImage = new Image(getClass().getResourceAsStream("/sprites/spaceship.png"));
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });
        scene.setOnMousePressed(e -> currentInput.shoot = true);
        scene.setOnMouseReleased(e -> currentInput.shoot = false);

        connectToServer();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateInput();
                render(gc);
            }
        }.start();

        primaryStage.setTitle("Space Shooter Multiplayer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 12345);
                out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                out.writeObject(new Message(Message.Type.CONNECT, playerName));
                out.flush();

                while (true) {
                    Message msg = (Message) in.readObject();
                    if (msg.type == Message.Type.STATE_UPDATE) {
                        this.gameState = (GameState) msg.data;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateInput() {
        currentInput.up = pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP);
        currentInput.down = pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN);
        currentInput.left = pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT);
        currentInput.right = pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT);
        currentInput.mouseX = mouseX;
        currentInput.mouseY = mouseY;

        if (out != null) {
            try {
                out.reset();
                out.writeObject(new Message(Message.Type.INPUT, currentInput));
                out.flush();
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    private void render(GraphicsContext gc) {
        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, 800, 600);

        // PowerUps
        for (PowerUp p : gameState.getPowerUps()) {
            gc.setFill(p.getType() == PowerUp.Type.HEALTH ? Color.GREEN : Color.YELLOW);
            gc.fillOval(p.getPosition().x - p.getRadius(), p.getPosition().y - p.getRadius(), p.getRadius()*2, p.getRadius()*2);
        }

        // Bullets
        gc.setFill(Color.WHITE);
        for (Bullet b : gameState.getBullets()) {
            gc.fillOval(b.getPosition().x - b.getRadius(), b.getPosition().y - b.getRadius(), b.getRadius()*2, b.getRadius()*2);
        }

        // Players
        for (SpaceShip ship : gameState.getPlayers().values()) {
            gc.save();
            gc.translate(ship.getPosition().x, ship.getPosition().y);
            gc.rotate(ship.getRotation());
            
            // Ship body
            if (shipImage != null) {
                gc.drawImage(shipImage, -20, -20, 40, 40);
            } else {
                gc.setFill(Color.BLUE);
                gc.fillPolygon(new double[]{-15, 20, -15}, new double[]{-15, 0, 15}, 3);
            }
            
            gc.restore();

            // Health bar
            gc.setFill(Color.RED);
            gc.fillRect(ship.getPosition().x - 20, ship.getPosition().y - 30, 40, 5);
            gc.setFill(Color.LIME);
            gc.fillRect(ship.getPosition().x - 20, ship.getPosition().y - 30, 40 * (ship.getHealth() / 100.0), 5);

            // Name and Score
            gc.setFill(Color.WHITE);
            gc.fillText(ship.getPlayerName() + " (" + ship.getScore() + ")", ship.getPosition().x - 20, ship.getPosition().y - 35);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
