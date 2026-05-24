// ShooterAlien.java
package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ShooterAlien extends GameObject {
    private int hp = 15;
    private int speedX = 4;
    private boolean movingRight = true;
    private long lastShootTime;
    private int shootDelay;
    private static Image shooterImage = null;

    public ShooterAlien(int x, int y) {
        super(x, y, 60, 60);
        lastShootTime = System.currentTimeMillis();
        shootDelay = 2000 + (int)(Math.random() * 3000);

        if (shooterImage == null) {
            try {
                File imageFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\alien_shooter.png");
                if (imageFile.exists()) shooterImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                System.out.println("Error loading shooter alien image: " + e.getMessage());
            }
        }
    }

    @Override
    public void update() {
        if (movingRight) {
            x += speedX;
            if (x + width >= Main.WINDOW_WIDTH) movingRight = false;
        } else {
            x -= speedX;
            if (x <= 0) movingRight = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (shooterImage != null) {
            g.drawImage(shooterImage, x, y, width, height, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, width, height);
        }
    }

    public void takeDamage() { hp--; }
    public int getHp() { return hp; }

    public boolean readyToShoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShootTime >= shootDelay) {
            lastShootTime = currentTime;
            return true;
        }
        return false;
    }
}