// InstructionsPanel.java
package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.Clip;

public class InstructionsPanel extends JPanel {
    private JTextArea instructionsArea;
    private JButton playButton;
    private String fullText;
    private boolean isAnimating = false;
    private volatile boolean instantComplete = false;
    private Image arcadeImage;
    private Clip typingClip;

    public InstructionsPanel(CardLayout cardLayout, JPanel container, GamePanel gamePanel, int width, int height) {
        this.setLayout(null);
        this.setFocusable(true);
        this.setBackground(Color.BLACK);

        try {
            File arcadeFile = new File("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\arcade_frame.png");
            if (arcadeFile.exists()) arcadeImage = ImageIO.read(arcadeFile);
        } catch (IOException e) {
            System.out.println("Error loading arcade image: " + e.getMessage());
        }

        fullText = "REBEL COMMAND TRANSMISSION:\n" +
                "------------------------------------------\n" +
                " > Pilot, the Imperial fleet invading our sector.\n" +
                " > Use W, A, S, D to navigate your starfighter.\n" +
                " > Press 'R' to fire your blaster cannons.\n" +
                " > Reach LEVEL 5 to assault the DEATH STAR.\n\n" +
                " > May the Force be with you.\n\n" +
                "AWAITING HYPERDRIVE ACTIVATION...";

        int textX = width * 2 / 10;
        int textY = height * 2 / 10 + 50;
        int textW = width * 6 / 10;
        int textH = height * 5 / 10;

        instructionsArea = new JTextArea();
        instructionsArea.setFont(new Font("Consolas", Font.BOLD, 36));
        instructionsArea.setForeground(new Color(0, 255, 255));
        instructionsArea.setOpaque(false);
        instructionsArea.setEditable(false);
        instructionsArea.setFocusable(false);
        instructionsArea.setBounds(textX, textY, textW, textH);
        this.add(instructionsArea);

        playButton = new JButton(" START ");
        playButton.setFont(FontManager.loadFont("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\arcadeclassic\\ARCADECLASSIC.TTF", 30f));
        playButton.setBackground(Color.BLACK);
        playButton.setForeground(Color.YELLOW);
        playButton.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
        playButton.setBounds(width / 2 - 250, textY + textH, 500, 70);
        playButton.setVisible(false);
        this.add(playButton);

        Runnable launchAction = () -> {
            if (typingClip != null && typingClip.isRunning()) {
                typingClip.stop();
            }
            cardLayout.show(container, "Game");
            gamePanel.startGame();
            gamePanel.requestFocusInWindow();
        };

        playButton.addActionListener(e -> launchAction.run());

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (isAnimating) instantComplete = true;
                    else if (playButton.isVisible()) launchAction.run();
                }
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                requestFocusInWindow();
            }
        });
    }

    public void startTextAnimation() {
        instructionsArea.setText("");
        playButton.setVisible(false);
        isAnimating = true;
        instantComplete = false;

        if (typingClip != null && typingClip.isRunning()) typingClip.stop();
        typingClip = SoundManager.playSound("C:\\Users\\adams\\IdeaProjects\\game\\src\\main\\resources\\typing-sound.wav", true);

        new Thread(() -> {
            for (int i = 0; i <= fullText.length(); i++) {
                if (instantComplete) break;
                final int index = i;
                SwingUtilities.invokeLater(() -> {
                    if (!instantComplete) instructionsArea.setText(fullText.substring(0, index) + "█");
                });
                try { Thread.sleep(25); } catch (InterruptedException e) { e.printStackTrace(); }
            }
            isAnimating = false;

            if (typingClip != null && typingClip.isRunning()) {
                typingClip.stop();
            }

            SwingUtilities.invokeLater(() -> {
                instructionsArea.setText(fullText);
                playButton.setVisible(true);
            });
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (arcadeImage != null) {
            g.drawImage(arcadeImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}