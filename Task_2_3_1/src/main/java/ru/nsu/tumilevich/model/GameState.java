package ru.nsu.tumilevich.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameState implements Serializable {
    private final ConcurrentHashMap<String, SpaceShip> players = new ConcurrentHashMap<>();
    private final List<Bullet> bullets = new ArrayList<>();
    private final List<PowerUp> powerUps = new ArrayList<>();
    private final double worldWidth = 800;
    private final double worldHeight = 600;

    public ConcurrentHashMap<String, SpaceShip> getPlayers() { return players; }
    public List<Bullet> getBullets() { return bullets; }
    public List<PowerUp> getPowerUps() { return powerUps; }
    public double getWorldWidth() { return worldWidth; }
    public double getWorldHeight() { return worldHeight; }
}
