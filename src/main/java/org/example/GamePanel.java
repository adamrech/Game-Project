// GamePanel.java
package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.sound.sampled.Clip;

public class GamePanel extends JPanel {
    private Thread gameThread;
    private boolean isRunning = false;
    private int backgroundY = 0;
    private Player player;
    private ArrayList<PlayerLaser> playerLasers = new ArrayList<>();
    private ArrayList<Alien> aliens = new ArrayList<>();
    private ArrayList<ShooterAlien> shooterAliens = new ArrayList<>();
    private ArrayList<EnemyProjectile> enemyProjectiles = new ArrayList<>();
    private BossAlien boss = null;
    private boolean wasBossFiringLasers = false;
    private long lastShootTime = 0;
    private KeyHandler keyHandler = new KeyHandler();
    private CardLayout cardLayout;
    private JPanel container;
    private boolean isGameOver = false;
    private int currentLevel = 1;
    private long lastDiveTime = 0;
    private Clip backgroundMusic;

    public GamePanel(int width, int height, CardLayout cardLayout, JPanel container) {
        this.setBounds(0, 0, width, height);
        this.setBackground(new Color(10, 10, 30));
        this.setFocusable(true);
        this.cardLayout = cardLayout;
        this.container = container;
        this.addKeyListener(keyHandler);
        resetGame();
    }

    public void resetGame() {
        player = new Player(getWidth() / 2 - 25, getHeight() - 150);
        isGameOver = false;
        keyHandler.enterPressed = false;
        currentLevel = 1;
        loadLevel(currentLevel);
    }

    public void loadLevel(int level) {
        aliens.clear();
        shooterAliens.clear();
        enemyProjectiles.clear();
        playerLasers.clear();
        boss = null;
        wasBossFiringLasers = false;

        int w = getWidth() > 0 ? getWidth() : Main.WINDOW_WIDTH;

        if (level == 5) {
            boss = new BossAlien(w / 2 - 100, 50);
            SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\boss_entrance.wav", false);
            return;
        }

        int rows = 3;
        int cols = 10;
        int alienW = 50;
        int usableWidth = w * 8 / 10;
        int startX = w / 10;
        int spacingX = usableWidth / (Math.max(1, cols - 1));
        int spacingY = 70;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int xPos = startX + (col * spacingX) - (alienW / 2);
                int yPos = 80 + (row * spacingY);

                if (level == 1) {
                    aliens.add(new Alien(xPos, yPos));
                } else {
                    if (Math.random() < 0.3) {
                        shooterAliens.add(new ShooterAlien(xPos, yPos));
                    } else {
                        aliens.add(new Alien(xPos, yPos));
                    }
                }
            }
        }
    }

    public void startGame() {
        if (gameThread == null || !isRunning) {
            if (isGameOver) resetGame();
            isRunning = true;

            if (backgroundMusic == null || !backgroundMusic.isRunning()) {
                backgroundMusic = SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\background_music.wav", true);
            }

            gameThread = new Thread(() -> {
                while (isRunning) {
                    update();
                    repaint();
                    try { Thread.sleep(16); } catch (InterruptedException e) { e.printStackTrace(); }
                }
            });
            gameThread.start();
        }
    }

    public void stopGame() {
        isRunning = false;
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    private void update() {
        if (isGameOver) {
            if (keyHandler.enterPressed) {
                stopGame();
                cardLayout.show(container, "Menu");
            }
            return;
        }

        if (keyHandler.skipLevelPressed) {
            aliens.clear();
            shooterAliens.clear();
            boss = null;
            keyHandler.skipLevelPressed = false;
        }

        backgroundY += 3;
        if (backgroundY >= getHeight()) backgroundY = 0;

        player.setMovement(keyHandler.upPressed, keyHandler.downPressed, keyHandler.leftPressed, keyHandler.rightPressed);
        player.update();

        if (keyHandler.isShooting) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShootTime >= 30) {
                playerLasers.add(new PlayerLaser(player.getX() + 22, player.getY()));
                lastShootTime = currentTime;
                SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\laser_shot.wav", false);
            }
        }

        for (int i = 0; i < playerLasers.size(); i++) {
            PlayerLaser laser = playerLasers.get(i);
            laser.update();
            if (laser.getY() < 0) { playerLasers.remove(i); i--; }
        }

        if (boss != null) {
            boss.updateBoss(player);

            ArrayList<EnemyProjectile> newBossShots = boss.attack(player);
            if (!newBossShots.isEmpty()) {
                enemyProjectiles.addAll(newBossShots);
                SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\boss_normal_attack.wav", false);
            }

            if (boss.getBounds().intersects(player.getBounds())) {
                player.loseLife();
                if (player.getLives() <= 0) isGameOver = true;
            }

            if (boss.isFiringLasers) {
                if (!wasBossFiringLasers) {
                    SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\boss_laser_attack.wav", false);
                }

                for (int i = 0; i < 3; i++) {
                    if (boss.laserBounds[i] != null && player.intersectsShape(boss.laserBounds[i])) {
                        player.loseLife();
                        if (player.getLives() <= 0) isGameOver = true;
                    }
                }
            }

            wasBossFiringLasers = boss.isFiringLasers;

            for (int j = 0; j < playerLasers.size(); j++) {
                PlayerLaser laser = playerLasers.get(j);
                if (laser.getBounds().intersects(boss.getBounds())) {
                    boss.takeDamage();
                    playerLasers.remove(j); j--;
                    if (boss.getHp() <= 0) {
                        boss = null;
                        break;
                    }
                }
            }
        }

        if (currentLevel >= 2 && boss == null) {
            int currentDiving = 0;
            for (Alien alien : aliens) {
                if (alien.isDiving()) currentDiving++;
            }

            int maxDiving = currentLevel - 1;
            if (maxDiving < 1) maxDiving = 1;

            if (currentDiving < maxDiving) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastDiveTime > 800) {
                    ArrayList<Alien> availableAliens = new ArrayList<>();
                    for (Alien alien : aliens) {
                        if (!alien.isDiving()) availableAliens.add(alien);
                    }

                    if (!availableAliens.isEmpty()) {
                        Alien chosen = availableAliens.get((int)(Math.random() * availableAliens.size()));
                        chosen.dive();
                        lastDiveTime = currentTime;
                    }
                }
            }
        }

        for (int i = 0; i < aliens.size(); i++) {
            Alien alien = aliens.get(i);
            alien.update();

            if (alien.getBounds().intersects(player.getBounds())) {
                player.loseLife();
                aliens.remove(i); i--;
                if (player.getLives() <= 0) isGameOver = true;
                continue;
            }

            for (int j = 0; j < playerLasers.size(); j++) {
                PlayerLaser laser = playerLasers.get(j);
                if (laser.getBounds().intersects(alien.getBounds())) {
                    alien.takeDamage();
                    playerLasers.remove(j); j--;
                    if (alien.getHp() <= 0) { aliens.remove(i); i--; break; }
                }
            }
        }

        for (int i = 0; i < shooterAliens.size(); i++) {
            ShooterAlien sa = shooterAliens.get(i);
            sa.update();

            if (sa.readyToShoot()) {
                enemyProjectiles.add(new EnemyProjectile(sa.getX() + sa.width / 2 - 7, sa.getY() + sa.height));
                SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\enemy_laser.wav", false);
            }

            if (sa.getBounds().intersects(player.getBounds())) {
                player.loseLife();
                shooterAliens.remove(i); i--;
                if (player.getLives() <= 0) isGameOver = true;
                continue;
            }

            for (int j = 0; j < playerLasers.size(); j++) {
                PlayerLaser laser = playerLasers.get(j);
                if (laser.getBounds().intersects(sa.getBounds())) {
                    sa.takeDamage();
                    playerLasers.remove(j); j--;
                    if (sa.getHp() <= 0) { shooterAliens.remove(i); i--; break; }
                }
            }
        }

        for (int i = 0; i < enemyProjectiles.size(); i++) {
            EnemyProjectile proj = enemyProjectiles.get(i);
            proj.update();

            if (proj.getY() > getHeight()) {
                enemyProjectiles.remove(i); i--;
                continue;
            }

            if (proj.getBounds().intersects(player.getBounds())) {
                player.loseLife();
                enemyProjectiles.remove(i); i--;
                if (player.getLives() <= 0) isGameOver = true;
            }
        }

        if (aliens.isEmpty() && shooterAliens.isEmpty() && boss == null && !isGameOver) {
            currentLevel++;
            if (currentLevel > 5) {
                isGameOver = true;
            } else {
                loadLevel(currentLevel);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        for (int i = 0; i < 15; i++) {
            int starY = (backgroundY + (i * 70)) % getHeight();
            g.fillOval(100 + (i * 40), starY, 3, 3);
            g.fillOval(700 - (i * 30), (starY + 30) % getHeight(), 4, 4);
        }

        if (player != null) player.draw(g);
        for (PlayerLaser laser : playerLasers) laser.draw(g);
        for (Alien alien : aliens) alien.draw(g);
        for (ShooterAlien sa : shooterAliens) sa.draw(g);
        for (EnemyProjectile proj : enemyProjectiles) proj.draw(g);

        if (boss != null) boss.draw(g);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("LIVES: " + (player != null ? player.getLives() : 0), 20, 40);

        g.setColor(Color.YELLOW);
        g.drawString("LEVEL: " + currentLevel, 20, 70);

        if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            String mainMsg = currentLevel > 5 ? "MISSION ACCOMPLISHED!" : "GAME OVER";
            g.setColor(currentLevel > 5 ? Color.GREEN : Color.RED);

            g.setFont(FontManager.loadFont("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\arcadeclassic\\ARCADECLASSIC.TTF", 100f));
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int x = (getWidth() - metrics.stringWidth(mainMsg)) / 2;
            g.drawString(mainMsg, x, getHeight() / 2 - 50);

            g.setColor(Color.WHITE);
            g.setFont(FontManager.loadFont("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\arcadeclassic\\ARCADECLASSIC.TTF", 30f));
            String promptMsg = "> PRESS [ENTER] TO RETURN TO HQ <";
            FontMetrics promptMetrics = g.getFontMetrics(g.getFont());
            int promptX = (getWidth() - promptMetrics.stringWidth(promptMsg)) / 2;
            g.drawString(promptMsg, promptX, getHeight() / 2 + 50);
        }
    }
}