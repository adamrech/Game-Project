// Alien.java
package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Alien extends GameObject {
    private int hp = 10;
    private int speedX = 4;
    private int speedY = 7;
    private boolean movingRight = true;
    private int startY;
    private boolean isDiving = false;
    private boolean isReturning = false;
    private static Image sharedAlienImage = null;

    public Alien(int x, int y) {
        super(x, y, 50, 50);
        this.startY = y;

        if (sharedAlienImage == null) {
            try {
                File imageFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\imprial_normal_ship.png");
                if (imageFile.exists()) sharedAlienImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                System.out.println("Error loading alien image: " + e.getMessage());
            }
        }
    }

    public void dive() {
        if (!isDiving) {
            isDiving = true;
            isReturning = false;
        }
    }

    public boolean isDiving() {
        return isDiving;
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

        if (isDiving) {
            if (!isReturning) {
                y += speedY;
                if (y > Main.WINDOW_HEIGHT - height) {
                    isReturning = true;
                }
            } else {
                y -= speedY;
                if (y <= startY) {
                    y = startY;
                    isDiving = false;
                    isReturning = false;
                }
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        if (sharedAlienImage != null) {
            g.drawImage(sharedAlienImage, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(x, y, width, height);
        }
    }

    public void takeDamage() { hp--; }
    public int getHp() { return hp; }
}