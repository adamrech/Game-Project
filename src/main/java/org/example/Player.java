// Player.java
package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Player extends GameObject {
    private int speed = 6;
    private int lives = 10;
    private boolean up, down, left, right;
    private Image playerImage;
    private long invincibilityTime = 0;

    public Player(int x, int y) {
        super(x, y, 60, 60);
        try {
            File imageFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\rabel_ship.png");
            if (imageFile.exists()) playerImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println("Error loading player image: " + e.getMessage());
        }
    }

    public void setMovement(boolean up, boolean down, boolean left, boolean right) {
        this.up = up; this.down = down; this.left = left; this.right = right;
    }

    public boolean intersectsShape(Shape shape) {
        if (shape == null) return false;
        java.awt.geom.Area areaA = new java.awt.geom.Area(getBounds());
        java.awt.geom.Area areaB = new java.awt.geom.Area(shape);
        return areaA.intersects(areaB.getBounds2D());
    }

    @Override
    public void update() {
        if (up && y > 0) y -= speed;
        if (down && y < Main.WINDOW_HEIGHT - height - 40) y += speed;
        if (left && x > 0) x -= speed;
        if (right && x < Main.WINDOW_WIDTH - width - 15) x += speed;
    }

    @Override
    public void draw(Graphics g) {
        if (System.currentTimeMillis() - invincibilityTime < 1500) {
            if ((System.currentTimeMillis() / 150) % 2 == 0) return;
        }

        if (playerImage != null) {
            g.drawImage(playerImage, x, y, width, height, null);
        } else {
            g.setColor(Color.CYAN);
            int[] xPoints = {x + width / 2, x, x + width};
            int[] yPoints = {y, y + height, y + height};
            g.fillPolygon(xPoints, yPoints, 3);
        }
    }

    public int getLives() { return lives; }

    public void loseLife() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - invincibilityTime > 1500) {
            lives--;
            invincibilityTime = currentTime;
        }
    }
}