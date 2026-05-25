package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.Clip;

public class MenuPanel extends JPanel {
    private Image backgroundImage;
    private boolean showText = true;
    private Clip menuMusicClip;

    public MenuPanel(CardLayout cardLayout, JPanel container, InstructionsPanel instructionsPanel, int width, int height) {
        this.setLayout(null);
        this.setFocusable(true);

        try {
            File imageFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\space_bg.png");
            if (imageFile.exists()) {
                backgroundImage = ImageIO.read(imageFile);
            }
        } catch (IOException e) {
            System.out.println("Menu Error loading image: " + e.getMessage());
        }

        menuMusicClip = SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\openning_music.wav", false);

        Runnable startAction = () -> {
            if (menuMusicClip != null && menuMusicClip.isRunning()) {
                menuMusicClip.stop();
            }
            cardLayout.show(container, "Instructions");
            instructionsPanel.startTextAnimation();
        };

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startAction.run();
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
            }
        });

        new Thread(() -> {
            while (true) {
                showText = !showText;
                repaint();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        g.setColor(Color.YELLOW);
        g.setFont(FontManager.loadFont("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\arcadeclassic\\ARCADECLASSIC.TTF", 80f));
        FontMetrics metrics1 = g.getFontMetrics(g.getFont());
        String title1 = "GALAXY ATTACK";
        int title1X = (getWidth() - metrics1.stringWidth(title1)) / 2;
        int titleY = getHeight() / 4 + 50;
        g.drawString(title1, title1X, titleY);

        g.setFont(FontManager.loadFont("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\arcadeclassic\\ARCADECLASSIC.TTF", 40f));
        FontMetrics metrics2 = g.getFontMetrics(g.getFont());
        String title2 = " STAR  WARS  EDITION ";
        int title2X = (getWidth() - metrics2.stringWidth(title2)) / 2;
        g.drawString(title2, title2X, titleY + 60);

        if (showText) {
            g.setColor(Color.WHITE);
            Font myArcadeFont = FontManager.loadFont("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\arcadeclassic\\ARCADECLASSIC.TTF", 40f);
            g.setFont(myArcadeFont);
            String prompt = " START GAME ";
            FontMetrics promptMetrics = g.getFontMetrics(g.getFont());
            int promptX = (getWidth() - promptMetrics.stringWidth(prompt)) / 2;
            g.drawString(prompt, promptX, getHeight() / 2 + 50);
        }
    }
}