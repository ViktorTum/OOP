package ru.nsu.tumilevich.model;

import ru.nsu.tumilevich.config.Config;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ConcurrentHashMap<String, SpaceShip> players = new ConcurrentHashMap<>();
    private final List<Bullet> bullets = new CopyOnWriteArrayList<>();
    private final List<PowerUp> powerUps = new CopyOnWriteArrayList<>();
    private volatile double worldWidth = Config.WORLD_WIDTH;
    private volatile double worldHeight = Config.WORLD_HEIGHT;

    public ConcurrentHashMap<String, SpaceShip> getPlayers() { return players; }
    public List<Bullet> getBullets() { return bullets; }
    public List<PowerUp> getPowerUps() { return powerUps; }
    public double getWorldWidth() { return worldWidth; }
    public double getWorldHeight() { return worldHeight; }

    public void expandWorldTo(double width, double height) {
        if (width > 0) {
            worldWidth = Math.max(worldWidth, width);
        }
        if (height > 0) {
            worldHeight = Math.max(worldHeight, height);
        }
    }
}
