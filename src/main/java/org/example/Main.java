// Main.java
package org.example;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static int WINDOW_WIDTH;
    public static int WINDOW_HEIGHT;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            WINDOW_WIDTH = screenSize.width;
            WINDOW_HEIGHT = screenSize.height;

            JFrame frame = new JFrame("Galaxy Attack");
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            CardLayout cardLayout = new CardLayout();
            JPanel mainContainer = new JPanel(cardLayout);

            GamePanel gamePanel = new GamePanel(WINDOW_WIDTH, WINDOW_HEIGHT, cardLayout, mainContainer);
            InstructionsPanel instructionsPanel = new InstructionsPanel(cardLayout, mainContainer, gamePanel, WINDOW_WIDTH, WINDOW_HEIGHT);
            MenuPanel menuPanel = new MenuPanel(cardLayout, mainContainer, instructionsPanel, WINDOW_WIDTH, WINDOW_HEIGHT);

            mainContainer.add(menuPanel, "Menu");
            mainContainer.add(instructionsPanel, "Instructions");
            mainContainer.add(gamePanel, "Game");

            frame.add(mainContainer);
            frame.setVisible(true);

            cardLayout.show(mainContainer, "Menu");
        });
    }
}