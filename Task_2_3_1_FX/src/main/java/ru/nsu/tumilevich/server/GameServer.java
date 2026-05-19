package ru.nsu.tumilevich.server;

import ru.nsu.tumilevich.model.*;
import ru.nsu.tumilevich.network.Message;
import ru.nsu.tumilevich.config.Config;
import ru.nsu.tumilevich.network.PlayerInput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private final GameState gameState = new GameState();
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private final Map<String, PlayerInput> latestInputs = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public void start() {
        Thread gameThread = new Thread(this::gameLoop, "game-loop");
        gameThread.setDaemon(true);
        gameThread.start();

        try (ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT)) {
            System.out.println("Server started on port " + Config.SERVER_PORT);
            while (running) {
                Socket socket = serverSocket.accept();
                String clientId = UUID.randomUUID().toString();
                ClientHandler handler = new ClientHandler(socket, clientId);
                clients.put(clientId, handler);
                new Thread(handler, "client-" + clientId).start();
            }
        } catch (IOException e) {
            System.err.println("Server stopped: " + e.getMessage());
        }
    }

    private void gameLoop() {
        long lastTime = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            double deltaTime = Math.min((now - lastTime) / 1_000_000_000.0, 0.05);
            lastTime = now;

            update(deltaTime, now);
            broadcastState();

            try {
                Thread.sleep(Config.SERVER_FRAME_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void update(double deltaTime, long nowNanos) {
        for (Map.Entry<String, SpaceShip> entry : gameState.getPlayers().entrySet()) {
            SpaceShip ship = entry.getValue();
            ship.updateBuffs(nowNanos);
            applyInput(entry.getKey(), ship, deltaTime, nowNanos);
        }

        for (Bullet bullet : gameState.getBullets()) {
            bullet.update(deltaTime);

            if (isOutsideWorld(bullet.getPosition())) {
                gameState.getBullets().remove(bullet);
                continue;
            }

            for (Map.Entry<String, SpaceShip> entry : gameState.getPlayers().entrySet()) {
                SpaceShip ship = entry.getValue();
                if (!entry.getKey().equals(bullet.getOwnerId()) && bullet.collidesWith(ship)) {
                    ship.takeDamage(bullet.getDamage());
                    gameState.getBullets().remove(bullet);
                    if (ship.isDead()) {
                        SpaceShip killer = gameState.getPlayers().get(bullet.getOwnerId());
                        if (killer != null) {
                            killer.addScore(Config.KILL_SCORE);
                        }
                        respawnPlayer(ship);
                    }
                    break;
                }
            }
        }

        if (gameState.getPowerUps().size() < Config.MAX_POWER_UPS && Math.random() < Config.POWER_UP_SPAWN_CHANCE_PER_TICK) {
            gameState.getPowerUps().add(new PowerUp(randomPosition(), randomPowerUpType()));
        }

        for (SpaceShip ship : gameState.getPlayers().values()) {
            for (PowerUp powerUp : gameState.getPowerUps()) {
                if (ship.collidesWith(powerUp)) {
                    applyPowerUp(ship, powerUp, nowNanos);
                    gameState.getPowerUps().remove(powerUp);
                }
            }
        }
    }

    private boolean isOutsideWorld(Vector2D position) {
        return position.x < 0 || position.x > gameState.getWorldWidth()
                || position.y < 0 || position.y > gameState.getWorldHeight();
    }

    private Vector2D randomPosition() {
        double margin = Config.WORLD_SPAWN_MARGIN;
        return new Vector2D(
                margin + Math.random() * (gameState.getWorldWidth() - 2 * margin),
                margin + Math.random() * (gameState.getWorldHeight() - 2 * margin)
        );
    }

    private PowerUp.Type randomPowerUpType() {
        PowerUp.Type[] values = PowerUp.Type.values();
        return values[(int) (Math.random() * values.length)];
    }

    private void applyPowerUp(SpaceShip ship, PowerUp powerUp, long nowNanos) {
        switch (powerUp.getType()) {
            case HEALTH -> ship.setHealth(ship.getHealth() + Config.HEALTH_POWER_UP_AMOUNT);
            case SPEED -> ship.activateSpeedBuff(nowNanos);
            case DAMAGE -> ship.activateDamageBuff(nowNanos);
        }
    }

    private void respawnPlayer(SpaceShip ship) {
        ship.setHealth(ship.getMaxHealth());
        ship.setPosition(randomPosition());
    }

    private void broadcastState() {
        Message msg = new Message(Message.Type.STATE_UPDATE, gameState);
        for (ClientHandler client : clients.values()) {
            client.sendMessage(msg);
        }
    }

    private void applyInput(String playerId, SpaceShip ship, double deltaTime, long nowNanos) {
        PlayerInput input = latestInputs.get(playerId);
        if (input == null) {
            slowDown(ship, deltaTime);
            clampToWorld(ship);
            return;
        }

        gameState.expandWorldTo(input.viewportWidth, input.viewportHeight);

        double dx = input.mouseX - ship.getPosition().x;
        double dy = input.mouseY - ship.getPosition().y;
        if (Math.hypot(dx, dy) > 0.001) {
            ship.setRotation(Math.toDegrees(Math.atan2(dy, dx)));
        }

        double angle = Math.toRadians(ship.getRotation());
        double forwardX = Math.cos(angle);
        double forwardY = Math.sin(angle);
        double rightX = -forwardY;
        double rightY = forwardX;

        double inputX = 0;
        double inputY = 0;
        if (input.up) {
            inputX += forwardX;
            inputY += forwardY;
        }
        if (input.down) {
            inputX -= forwardX;
            inputY -= forwardY;
        }
        if (input.right) {
            inputX += rightX;
            inputY += rightY;
        }
        if (input.left) {
            inputX -= rightX;
            inputY -= rightY;
        }

        Vector2D velocity = ship.getVelocity();
        double inputLength = Math.hypot(inputX, inputY);
        if (inputLength > 0.001) {
            inputX /= inputLength;
            inputY /= inputLength;
            double acceleration = Config.PLAYER_ACCELERATION * ship.getSpeedMultiplier();
            velocity.x += inputX * acceleration * deltaTime;
            velocity.y += inputY * acceleration * deltaTime;
        } else {
            slowDown(ship, deltaTime);
        }

        double maxSpeed = Config.PLAYER_BASE_SPEED * ship.getSpeedMultiplier();
        double currentSpeed = Math.hypot(velocity.x, velocity.y);
        if (currentSpeed > maxSpeed) {
            velocity.x = velocity.x / currentSpeed * maxSpeed;
            velocity.y = velocity.y / currentSpeed * maxSpeed;
        }

        ship.getPosition().x += velocity.x * deltaTime;
        ship.getPosition().y += velocity.y * deltaTime;
        clampToWorld(ship);

        if (input.shoot && ship.canShoot(nowNanos)) {
            double bulletStartOffset = ship.getRadius() + Config.BULLET_SPAWN_OFFSET;
            Vector2D bulletPosition = new Vector2D(
                    ship.getPosition().x + forwardX * bulletStartOffset,
                    ship.getPosition().y + forwardY * bulletStartOffset
            );
            Vector2D bulletVelocity = new Vector2D(
                    forwardX * Config.BULLET_SPEED + velocity.x * Config.BULLET_INHERIT_SHIP_VELOCITY,
                    forwardY * Config.BULLET_SPEED + velocity.y * Config.BULLET_INHERIT_SHIP_VELOCITY
            );
            int damage = (int) Math.round(Config.BULLET_BASE_DAMAGE * ship.getDamageMultiplier());
            gameState.getBullets().add(new Bullet(bulletPosition, bulletVelocity, playerId, damage));
            ship.markShot(nowNanos);
        }
    }

    private void slowDown(SpaceShip ship, double deltaTime) {
        Vector2D velocity = ship.getVelocity();
        double factor = Math.max(0, 1.0 - Config.PLAYER_DRAG * deltaTime);
        velocity.x *= factor;
        velocity.y *= factor;
        if (Math.hypot(velocity.x, velocity.y) < Config.PLAYER_STOP_EPSILON) {
            velocity.x = 0;
            velocity.y = 0;
        }
    }

    private void clampToWorld(SpaceShip ship) {
        Vector2D position = ship.getPosition();
        double radius = ship.getRadius();
        double clampedX = Math.max(radius, Math.min(gameState.getWorldWidth() - radius, position.x));
        double clampedY = Math.max(radius, Math.min(gameState.getWorldHeight() - radius, position.y));
        if (clampedX != position.x) {
            ship.getVelocity().x = 0;
        }
        if (clampedY != position.y) {
            ship.getVelocity().y = 0;
        }
        position.x = clampedX;
        position.y = clampedY;
    }


    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final String clientId;
        private ObjectOutputStream out;

        private ClientHandler(Socket socket, String clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                while (running && !socket.isClosed()) {
                    Message msg = (Message) in.readObject();
                    handleMessage(msg);
                }
            } catch (Exception e) {
                System.out.println("Client disconnected: " + clientId);
            } finally {
                clients.remove(clientId);
                gameState.getPlayers().remove(clientId);
                latestInputs.remove(clientId);
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        private void handleMessage(Message msg) {
            if (msg.type == Message.Type.CONNECT) {
                String name = ((String) msg.data).trim();
                if (name.isEmpty()) {
                    name = "Player";
                }
                gameState.getPlayers().put(clientId, new SpaceShip(randomPosition(), name));
            } else if (msg.type == Message.Type.INPUT) {
                handleInput((PlayerInput) msg.data);
            }
        }

        private void handleInput(PlayerInput input) {
            if (gameState.getPlayers().containsKey(clientId)) {
                latestInputs.put(clientId, input);
            }
        }

        private void sendMessage(Message msg) {
            try {
                if (out != null) {
                    out.reset();
                    out.writeObject(msg);
                    out.flush();
                }
            } catch (IOException e) {
                clients.remove(clientId);
                gameState.getPlayers().remove(clientId);
                latestInputs.remove(clientId);
            }
        }
    }

    public static void main(String[] args) {
        new GameServer().start();
    }
}
