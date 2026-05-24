package org.example;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BossAlien extends GameObject {
    private int maxHp = 50; // 50 פגיעות כדי להרוג אותו!
    private int hp = maxHp;
    private int speedX = 6; // זז די מהר
    private boolean movingRight = true;
    
    private long lastShootTime;
    private int shootDelay = 700; // יורה מטאור כמעט כל חצי שנייה!
    
    private static Image bossImage = null;

    public BossAlien(int x, int y) {
        super(x, y, 200, 200); // בוס ענק! 200x200 פיקסלים
        
        lastShootTime = System.currentTimeMillis();

        if (bossImage == null) {
            try {
                File imageFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\mother_ship.png");
                if (imageFile.exists()) bossImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                System.out.println("Error loading boss image: " + e.getMessage());
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

    public boolean readyToShoot() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShootTime >= shootDelay) {
            lastShootTime = currentTime;
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics g) {
        if (bossImage != null) {
            g.drawImage(bossImage, x, y, width, height, null);
        } else {
            g.setColor(Color.DARK_GRAY);
            g.fillOval(x, y, width, height);
        }
        
        // ציור מד החיים של הבוס מעל הראש שלו
        g.setColor(Color.RED);
        g.fillRect(x, y - 20, width, 10); // רקע אדום (חיים שחסרים)
        
        g.setColor(Color.GREEN);
        int hpWidth = (int)((double)hp / maxHp * width);
        g.fillRect(x, y - 20, hpWidth, 10); // ירוק (חיים שנשארו)
    }

    public void takeDamage() { hp--; }
    public int getHp() { return hp; }
}