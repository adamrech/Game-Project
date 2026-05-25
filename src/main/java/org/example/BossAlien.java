// BossAlien.java
package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BossAlien extends GameObject {
    private int maxHp = 100;
    private int hp = maxHp;
    private int speedX = 6;
    private boolean movingRight = true;
    private long lastShootTime;
    private int shootDelay;
    private static Image bossImage = null;
    public boolean isFiringLasers = false;
    private long laserStartTime = 0;
    private long lastLaserTime = 0;
    private final int LASER_DURATION = 3000;
    public Polygon[] laserBounds = new Polygon[3];

    public BossAlien(int x, int y) {
        super(x, y, 200, 200);
        lastShootTime = System.currentTimeMillis();
        lastLaserTime = System.currentTimeMillis();

        if (bossImage == null) {
            try {
                File imageFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\daeth_star.png");
                if (imageFile.exists()) bossImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                System.out.println("Error loading boss image: " + e.getMessage());
            }
        }
    }

    @Override
    public void update() {}

    public void updateBoss(Player player) {
        int bossCenter = x + width / 2;
        int playerCenter = player.getX() + player.width / 2;

        if (hp <= maxHp / 2) {
            int currentSpeed = isFiringLasers ? speedX - 2 : speedX + 2;
            if (bossCenter < playerCenter - 15) x += currentSpeed;
            else if (bossCenter > playerCenter + 15) x -= currentSpeed;

            if (x < 0) x = 0;
            if (x + width > Main.WINDOW_WIDTH) x = Main.WINDOW_WIDTH - width;
        } else {
            if (movingRight) {
                x += speedX;
                if (x + width >= Main.WINDOW_WIDTH) movingRight = false;
            } else {
                x -= speedX;
                if (x <= 0) movingRight = true;
            }
        }

        long currentTime = System.currentTimeMillis();
        if (hp <= maxHp / 2 && !isFiringLasers && currentTime - lastLaserTime >= 4000) {
            isFiringLasers = true;
            laserStartTime = currentTime;
        }

        if (isFiringLasers) {
            if (currentTime - laserStartTime >= LASER_DURATION) {
                isFiringLasers = false;
                lastLaserTime = currentTime;
            }

            int centerX = bossCenter;
            int startY = y + height - 20;
            int endY = Main.WINDOW_HEIGHT;
            int laserWidthHalf = 15; int maxAngleOffset = 350;

            int[] lx = { centerX - laserWidthHalf, centerX + laserWidthHalf, centerX - maxAngleOffset + laserWidthHalf, centerX - maxAngleOffset - laserWidthHalf };
            int[] ly = { startY, startY, endY, endY };
            laserBounds[0] = new Polygon(lx, ly, 4);

            int[] cx = { centerX - laserWidthHalf, centerX + laserWidthHalf, centerX + laserWidthHalf, centerX - laserWidthHalf };
            int[] cy = { startY, startY, endY, endY };
            laserBounds[1] = new Polygon(cx, cy, 4);

            int[] rx = { centerX - laserWidthHalf, centerX + laserWidthHalf, centerX + maxAngleOffset + laserWidthHalf, centerX + maxAngleOffset - laserWidthHalf };
            int[] ry = { startY, startY, endY, endY };
            laserBounds[2] = new Polygon(rx, ry, 4);
        } else {
            laserBounds[0] = null;
            laserBounds[1] = null;
            laserBounds[2] = null;
        }
    }

    public ArrayList<EnemyProjectile> attack(Player player) {
        ArrayList<EnemyProjectile> spawnedProjectiles = new ArrayList<>();
        if (isFiringLasers) return spawnedProjectiles;

        long currentTime = System.currentTimeMillis();
        boolean isEnraged = hp <= maxHp / 2;
        shootDelay = isEnraged ? 400 : 700;

        if (currentTime - lastShootTime >= shootDelay) {
            lastShootTime = currentTime;
            int attackType = 1 + (int)(Math.random() * 2);
            int centerX = x + width / 2;
            int bottomY = y + height - 20;

            if (attackType == 1) {
                int diffX = (player.getX() + player.width / 2) - centerX;
                int diffY = (player.getY() + player.height / 2) - bottomY;
                double angle = Math.atan2(diffY, diffX);
                int projSpeedX = (int)(Math.cos(angle) * 12);
                int projSpeedY = (int)(Math.sin(angle) * 12);
                spawnedProjectiles.add(new EnemyProjectile(centerX - 10, bottomY, projSpeedX, projSpeedY, 25, 25, 1));
            } else if (attackType == 2) {
                spawnedProjectiles.add(new EnemyProjectile(centerX - 10, bottomY, -6, 9, 20, 20, 2));
                spawnedProjectiles.add(new EnemyProjectile(centerX - 10, bottomY, -3, 10, 20, 20, 2));
                spawnedProjectiles.add(new EnemyProjectile(centerX - 10, bottomY, 0, 11, 20, 20, 2));
                spawnedProjectiles.add(new EnemyProjectile(centerX - 10, bottomY, 3, 10, 20, 20, 2));
                spawnedProjectiles.add(new EnemyProjectile(centerX - 10, bottomY, 6, 9, 20, 20, 2));
            }
        }
        return spawnedProjectiles;
    }

    @Override
    public void draw(Graphics g) {
        if (isFiringLasers) {
            Graphics2D g2 = (Graphics2D)g;
            for (int i = 0; i < 3; i++) {
                if (laserBounds[i] != null) {
                    g2.setColor(Color.RED);
                    g2.fillPolygon(laserBounds[i]);
                }
            }
        }

        if (bossImage != null) {
            g.drawImage(bossImage, x, y, width, height, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillOval(x, y, width, height);
        }

        g.setColor(Color.RED);
        g.fillRect(x, y - 20, width, 10);

        if (hp <= maxHp / 2) g.setColor(Color.ORANGE);
        else g.setColor(Color.GREEN);

        int hpWidth = (int)((double)hp / maxHp * width);
        g.fillRect(x, y - 20, hpWidth, 10);
    }

    public void takeDamage() { hp--; }
    public int getHp() { return hp; }
}