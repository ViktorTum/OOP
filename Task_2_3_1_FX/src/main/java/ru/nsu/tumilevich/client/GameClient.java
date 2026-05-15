package ru.nsu.tumilevich.client;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import ru.nsu.tumilevich.config.Config;
import ru.nsu.tumilevich.model.*;
import ru.nsu.tumilevich.network.Message;
import ru.nsu.tumilevich.network.PlayerInput;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class GameClient extends Application {
    private static GameClient instance;

    private Image shipImage;
    private volatile GameState gameState = new GameState();
    private final PlayerInput currentInput = new PlayerInput();
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private ObjectOutputStream out;
    private double mouseX, mouseY;
    private double canvasWidth = Config.DEFAULT_WINDOW_WIDTH;
    private double canvasHeight = Config.DEFAULT_WINDOW_HEIGHT;
    private boolean mouseShootPressed = false;
    private String playerName = "Player";
    private boolean connected = false;

    public static GameClient getInstance() {
        return instance;
    }

    public void setPlayerName(String name) {
        this.playerName = (name == null || name.trim().isEmpty()) ? "Player" : name.trim();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        showMenu(primaryStage);
    }

    public void showMenu(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/nsu/tumilevich/menu.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setTitle("Space Shooter Menu");
        stage.show();
    }

    public void startGame(Stage stage) {
        if (connectToServer()) {
            setupGameUI(stage);
        } else {
            System.err.println("Failed to connect to server. Is it running?");
        }
    }

    private void setupGameUI(Stage stage) {
        try {
            var resource = getClass().getResourceAsStream("/sprites/spaceship.png");
            if (resource != null) {
                shipImage = new Image(resource);
                System.out.println("Ship image loaded successfully.");
            } else {
                System.err.println("Ship image NOT found at /sprites/spaceship.png");
            }
        } catch (Exception e) {
            System.err.println("Error loading ship image: " + e.getMessage());
        }
        
        Canvas canvas = new Canvas();
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, Config.DEFAULT_WINDOW_WIDTH, Config.DEFAULT_WINDOW_HEIGHT);

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });
        scene.setOnMouseDragged(e -> {
            // During shooting JavaFX sends drag events instead of move events.
            // Keep aim updating so the ship can turn and fire at the same time.
            mouseX = e.getX();
            mouseY = e.getY();
        });
        scene.setOnMousePressed(e -> mouseShootPressed = true);
        scene.setOnMouseReleased(e -> mouseShootPressed = false);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateInput();
                render(gc);
            }
        }.start();

        stage.setScene(scene);
        stage.setTitle("Space Shooter Multiplayer");

        stage.setFullScreenExitHint("Нажмите ESC для выхода из полноэкранного режима");
        stage.setFullScreen(Config.FULLSCREEN);
        stage.setMaximized(true);
        stage.show();
    }

    private boolean connectToServer() {
        try {
            Socket socket = new Socket(Config.SERVER_IP, Config.SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(new Message(Message.Type.CONNECT, playerName));
            out.flush();
            connected = true;

            Thread receiver = new Thread(() -> {
                try {
                    while (connected) {
                        Message msg = (Message) in.readObject();
                        if (msg.type == Message.Type.STATE_UPDATE) {
                            this.gameState = (GameState) msg.data;
                        }
                    }
                } catch (Exception e) {
                    connected = false;
                    System.err.println("Connection closed: " + e.getMessage());
                }
            }, "server-listener");
            receiver.setDaemon(true);
            receiver.start();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateInput() {
        currentInput.up = pressedKeys.contains(KeyCode.W) || pressedKeys.contains(KeyCode.UP);
        currentInput.down = pressedKeys.contains(KeyCode.S) || pressedKeys.contains(KeyCode.DOWN);
        currentInput.left = pressedKeys.contains(KeyCode.A) || pressedKeys.contains(KeyCode.LEFT);
        currentInput.right = pressedKeys.contains(KeyCode.D) || pressedKeys.contains(KeyCode.RIGHT);
        currentInput.shoot = mouseShootPressed || pressedKeys.contains(KeyCode.SPACE);
        currentInput.viewportWidth = Math.max(1, canvasWidth);
        currentInput.viewportHeight = Math.max(1, canvasHeight);
        double worldWidth = Math.max(1, gameState.getWorldWidth());
        double worldHeight = Math.max(1, gameState.getWorldHeight());
        double scaleX = canvasWidth / worldWidth;
        double scaleY = canvasHeight / worldHeight;
        currentInput.mouseX = mouseX / Math.max(0.0001, scaleX);
        currentInput.mouseY = mouseY / Math.max(0.0001, scaleY);

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
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        canvasWidth = width;
        canvasHeight = height;

        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        double worldWidth = Math.max(1, gameState.getWorldWidth());
        double worldHeight = Math.max(1, gameState.getWorldHeight());
        double scaleX = width / worldWidth;
        double scaleY = height / worldHeight;

        gc.save();
        gc.scale(scaleX, scaleY);

        drawWorldBounds(gc, worldWidth, worldHeight);

        for (PowerUp p : gameState.getPowerUps()) {
            gc.setFill(powerUpColor(p));
            gc.fillOval(p.getPosition().x - p.getRadius(), p.getPosition().y - p.getRadius(), p.getRadius()*2, p.getRadius()*2);
            gc.setFill(Color.BLACK);
            gc.fillText(p.getType().name().substring(0, 1), p.getPosition().x - 4, p.getPosition().y + 4);
        }

        gc.setFill(Color.WHITE);
        for (Bullet b : gameState.getBullets()) {
            gc.fillOval(b.getPosition().x - b.getRadius(), b.getPosition().y - b.getRadius(), b.getRadius()*2, b.getRadius()*2);
        }

        for (SpaceShip ship : gameState.getPlayers().values()) {
            gc.save();
            gc.translate(ship.getPosition().x, ship.getPosition().y);
            // The sprite source is rotated by 90° relative to the game direction.
            // Add 90° clockwise only for drawing; bullet direction remains unchanged.
            gc.rotate(ship.getRotation() + Config.SHIP_SPRITE_ROTATION_OFFSET_DEGREES);
            
            if (shipImage != null) {
                gc.drawImage(shipImage, -Config.SHIP_DRAW_SIZE / 2, -Config.SHIP_DRAW_SIZE / 2, Config.SHIP_DRAW_SIZE, Config.SHIP_DRAW_SIZE);
            } else {
                gc.setFill(Color.BLUE);
                gc.fillPolygon(new double[]{-15, 20, -15}, new double[]{-15, 0, 15}, 3);
            }
            
            gc.restore();

            gc.setFill(Color.RED);
            gc.fillRect(ship.getPosition().x - Config.HEALTH_BAR_WIDTH / 2, ship.getPosition().y - Config.HEALTH_BAR_Y_OFFSET, Config.HEALTH_BAR_WIDTH, Config.HEALTH_BAR_HEIGHT);
            gc.setFill(Color.LIME);
            gc.fillRect(ship.getPosition().x - Config.HEALTH_BAR_WIDTH / 2, ship.getPosition().y - Config.HEALTH_BAR_Y_OFFSET, Config.HEALTH_BAR_WIDTH * (ship.getHealth() / (double) ship.getMaxHealth()), Config.HEALTH_BAR_HEIGHT);

            gc.setFill(Color.WHITE);
            gc.fillText(ship.getPlayerName() + " (" + ship.getScore() + ")", ship.getPosition().x - Config.HEALTH_BAR_WIDTH / 2, ship.getPosition().y - Config.NAME_Y_OFFSET);
        }

        gc.restore();
    }

    private void drawWorldBounds(GraphicsContext gc, double worldWidth, double worldHeight) {
        double wall = Config.WORLD_WALL_THICKNESS;
        gc.setFill(Color.rgb(55, 55, 75));
        gc.fillRect(0, 0, worldWidth, wall);
        gc.fillRect(0, worldHeight - wall, worldWidth, wall);
        gc.fillRect(0, 0, wall, worldHeight);
        gc.fillRect(worldWidth - wall, 0, wall, worldHeight);

        gc.setStroke(Color.rgb(125, 125, 170));
        gc.setLineWidth(3);
        gc.strokeRect(wall / 2.0, wall / 2.0, worldWidth - wall, worldHeight - wall);
    }

    private Color powerUpColor(PowerUp powerUp) {
        return switch (powerUp.getType()) {
            case HEALTH -> Color.LIMEGREEN;
            case SPEED -> Color.DEEPSKYBLUE;
            case DAMAGE -> Color.ORANGE;
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
