// MenuPanel.java
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
        g.setFont(new Font("Consolas", Font.BOLD, 80));
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        String title = "GALAXY ATTACK";
        int titleX = (getWidth() - metrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, getHeight() / 4 + 50);

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