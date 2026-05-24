// EnemyProjectile.java
package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class EnemyProjectile extends GameObject {
    private int speedY;
    private int speedX;
    private int type;
    private static Image meteorImage = null;

    public EnemyProjectile(int x, int y, int speedX, int speedY, int width, int height, int type) {
        super(x, y, width, height);
        this.speedX = speedX;
        this.speedY = speedY;
        this.type = type;

        if (meteorImage == null) {
            try {
                File imageFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\meteor.png");
                if (imageFile.exists()) {
                    meteorImage = ImageIO.read(imageFile);
                }
            } catch (IOException e) {
                System.out.println("Error loading meteor image: " + e.getMessage());
            }
        }
    }

    public EnemyProjectile(int x, int y) {
        this(x, y, 0, 8, 20, 40, 1);
    }

    @Override
    public void update() {
        x += speedX;
        y += speedY;
    }

    @Override
    public void draw(Graphics g) {
        if (type == 3) {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
            g.setColor(Color.WHITE);
            g.fillRect(x + width / 4, y, width / 2, height);
        } else if (type == 2) {
            g.setColor(Color.MAGENTA);
            g.fillOval(x, y, width, height);
        } else {
            if (meteorImage != null) {
                g.drawImage(meteorImage, x, y, width, height, null);
            } else {
                g.setColor(Color.ORANGE);
                g.fillOval(x, y, width, height);
            }
        }
    }
}