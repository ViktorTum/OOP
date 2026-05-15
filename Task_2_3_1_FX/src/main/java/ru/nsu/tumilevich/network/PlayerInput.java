package ru.nsu.tumilevich.network;

import java.io.Serializable;

public class PlayerInput implements Serializable {
    public boolean up, down, left, right, shoot;
    public double mouseX, mouseY;
    public double viewportWidth, viewportHeight;
}
