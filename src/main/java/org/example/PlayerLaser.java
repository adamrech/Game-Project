// PlayerLaser.java
package org.example;

import java.awt.*;

public class PlayerLaser extends GameObject {
    private int speed = 15;

    public PlayerLaser(int x, int y) {
        super(x, y, 5, 20);
    }

    @Override
    public void update() {
        y -= speed;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }
}