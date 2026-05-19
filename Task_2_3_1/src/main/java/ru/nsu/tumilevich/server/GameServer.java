package ru.nsu.tumilevich.server;

import ru.nsu.tumilevich.model.*;
import ru.nsu.tumilevich.network.Message;
import ru.nsu.tumilevich.network.PlayerInput;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameServer {
    private final int port = 12345;
    private final GameState gameState = new GameState();
    private final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    private boolean running = true;

    public void start() {
        new Thread(this::gameLoop).start();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (running) {
                Socket socket = serverSocket.accept();
                String clientId = UUID.randomUUID().toString();
                ClientHandler handler = new ClientHandler(socket, clientId);
                clients.put(clientId, handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameLoop() {
        long lastTime = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            update(deltaTime);
            broadcastState();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(double deltaTime) {
        // Update bullets
        Iterator<Bullet> bulletIt = gameState.getBullets().iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            b.update();
            
            // Check bounds
            if (b.getPosition().x < 0 || b.getPosition().x > gameState.getWorldWidth() ||
                b.getPosition().y < 0 || b.getPosition().y > gameState.getWorldHeight()) {
                bulletIt.remove();
                continue;
            }

            // Check collisions with players
            for (SpaceShip ship : gameState.getPlayers().values()) {
                if (!ship.getId().equals(b.getOwnerId()) && b.collidesWith(ship)) {
                    ship.takeDamage(10);
                    bulletIt.remove();
                    if (ship.isDead()) {
                        SpaceShip killer = gameState.getPlayers().get(b.getOwnerId());
                        if (killer != null) killer.addScore(100);
                        respawnPlayer(ship);
                    }
                    break;
                }
            }
        }

        // Spawn powerups
        if (gameState.getPowerUps().size() < 5 && Math.random() < 0.01) {
            gameState.getPowerUps().add(new PowerUp(
                new Vector2D(Math.random() * gameState.getWorldWidth(), Math.random() * gameState.getWorldHeight()),
                PowerUp.Type.values()[(int)(Math.random() * PowerUp.Type.values().length)]
            ));
        }

        // Check powerup collisions
        for (SpaceShip ship : gameState.getPlayers().values()) {
            Iterator<PowerUp> pIt = gameState.getPowerUps().iterator();
            while (pIt.hasNext()) {
                PowerUp p = pIt.next();
                if (ship.collidesWith(p)) {
                    if (p.getType() == PowerUp.Type.HEALTH) ship.setHealth(ship.getHealth() + 20);
                    pIt.remove();
                }
            }
        }
    }

    private void respawnPlayer(SpaceShip ship) {
        ship.setHealth(100);
        ship.setPosition(new Vector2D(Math.random() * gameState.getWorldWidth(), Math.random() * gameState.getWorldHeight()));
    }

    private void broadcastState() {
        Message msg = new Message(Message.Type.STATE_UPDATE, gameState);
        for (ClientHandler client : clients.values()) {
            client.sendMessage(msg);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final String clientId;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket, String clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                while (running) {
                    Message msg = (Message) in.readObject();
                    handleMessage(msg);
                }
            } catch (Exception e) {
                System.out.println("Client disconnected: " + clientId);
            } finally {
                clients.remove(clientId);
                gameState.getPlayers().remove(clientId);
            }
        }

        private void handleMessage(Message msg) {
            if (msg.type == Message.Type.CONNECT) {
                String name = (String) msg.data;
                gameState.getPlayers().put(clientId, new SpaceShip(new Vector2D(400, 300), name));
            } else if (msg.type == Message.Type.INPUT) {
                PlayerInput input = (PlayerInput) msg.data;
                SpaceShip ship = gameState.getPlayers().get(clientId);
                if (ship != null) {
                    double speed = 5;
                    if (input.up) ship.getPosition().y -= speed;
                    if (input.down) ship.getPosition().y += speed;
                    if (input.left) ship.getPosition().x -= speed;
                    if (input.right) ship.getPosition().x += speed;
                    
                    // Rotation towards mouse
                    double dx = input.mouseX - ship.getPosition().x;
                    double dy = input.mouseY - ship.getPosition().y;
                    ship.setRotation(Math.toDegrees(Math.atan2(dy, dx)));

                    if (input.shoot) {
                        double angle = Math.toRadians(ship.getRotation());
                        Vector2D vel = new Vector2D(Math.cos(angle) * 10, Math.sin(angle) * 10);
                        gameState.getBullets().add(new Bullet(new Vector2D(ship.getPosition().x, ship.getPosition().y), vel, clientId));
                    }
                }
            }
        }

        public void sendMessage(Message msg) {
            try {
                out.reset();
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                // Ignore
            }
        }
    }

    public static void main(String[] args) {
        new GameServer().start();
    }
}
