package ru.nsu.tumilevich.config;

public final class Config {
    private Config() {}

    // =========================
    // NETWORK
    // =========================
    // true  -> клиент подключается к этому же компьютеру: 127.0.0.1
    // false -> клиент подключается к NETWORK_SERVER_IP: локальная сеть / Tailscale / VPN
    public static final boolean LOCALHOST_MODE = true;
    public static final String LOCAL_SERVER_IP = "127.0.0.1";
    public static final String NETWORK_SERVER_IP = "100.100.154.104";
    public static final String SERVER_IP = LOCALHOST_MODE ? LOCAL_SERVER_IP : NETWORK_SERVER_IP;
    public static final int SERVER_PORT = 5555;

    // =========================
    // WINDOW / GRAPHICS
    // =========================
    public static final double DEFAULT_WINDOW_WIDTH = 1280;
    public static final double DEFAULT_WINDOW_HEIGHT = 720;
    public static final boolean FULLSCREEN = true;
    public static final double SHIP_DRAW_SIZE = 48.0;
    public static final double SHIP_SPRITE_ROTATION_OFFSET_DEGREES = 90.0;
    public static final double HEALTH_BAR_WIDTH = 56.0;
    public static final double HEALTH_BAR_HEIGHT = 6.0;
    public static final double HEALTH_BAR_Y_OFFSET = 42.0;
    public static final double NAME_Y_OFFSET = 50.0;
    public static final double CAMERA_SMOOTHING = 0.12;

    // =========================
    // WORLD
    // =========================
    public static final int WORLD_WIDTH = 1920;
    public static final int WORLD_HEIGHT = 1080;
    public static final double WORLD_WALL_THICKNESS = 14.0;
    public static final double WORLD_SPAWN_MARGIN = 80.0;

    // =========================
    // SERVER
    // =========================
    public static final int SERVER_TICKRATE = 60;
    public static final long SERVER_FRAME_DELAY_MS = 1000L / SERVER_TICKRATE;

    // =========================
    // PLAYER PHYSICS
    // =========================
    public static final double PLAYER_RADIUS = 24.0;
    public static final double PLAYER_ACCELERATION = 700.0;
    public static final double PLAYER_BASE_SPEED = 600.0;
    public static final double PLAYER_MAX_SPEED = PLAYER_BASE_SPEED;
    public static final double PLAYER_ROTATION_SPEED = 240.0;
    public static final double PLAYER_DRAG = 2.2;
    public static final double PLAYER_STOP_EPSILON = 4.0;
    public static final int PLAYER_MAX_HEALTH = 100;

    // =========================
    // PLAYER BUFFS
    // =========================
    public static final double PLAYER_SPEED_BUFF_MULTIPLIER = 1.6;
    public static final double PLAYER_DAMAGE_BUFF_MULTIPLIER = 2.0;
    public static final long PLAYER_BUFF_DURATION_NANOS = 7_000_000_000L;
    public static final int HEALTH_POWER_UP_AMOUNT = 30;

    // =========================
    // BULLETS / COMBAT
    // =========================
    public static final double BULLET_SPEED = 900.0;
    public static final int BULLET_BASE_DAMAGE = 10;
    public static final int BULLET_DAMAGE = BULLET_BASE_DAMAGE;
    public static final double BULLET_SPAWN_OFFSET = 8.0;
    public static final double BULLET_INHERIT_SHIP_VELOCITY = 0.25;
    public static final double SHOOT_COOLDOWN = 0.15;
    public static final long SHOOT_COOLDOWN_NANOS = (long) (SHOOT_COOLDOWN * 1_000_000_000L);

    // =========================
    // GAMEPLAY
    // =========================
    public static final int MAX_POWER_UPS = 8;
    public static final double POWER_UP_SPAWN_CHANCE_PER_TICK = 0.015;
    public static final int KILL_SCORE = 1;
}
