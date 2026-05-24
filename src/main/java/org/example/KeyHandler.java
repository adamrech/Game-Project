// KeyHandler.java
package org.example;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyHandler extends KeyAdapter {
    public static boolean upPressed, downPressed, leftPressed, rightPressed, isShooting, enterPressed, skipLevelPressed;

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: upPressed = true; break;
            case KeyEvent.VK_S: downPressed = true; break;
            case KeyEvent.VK_A: leftPressed = true; break;
            case KeyEvent.VK_D: rightPressed = true; break;
            case KeyEvent.VK_R: isShooting = true; break;
            case KeyEvent.VK_ENTER: enterPressed = true; break;
            case KeyEvent.VK_P: skipLevelPressed = true; break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: upPressed = false; break;
            case KeyEvent.VK_S: downPressed = false; break;
            case KeyEvent.VK_A: leftPressed = false; break;
            case KeyEvent.VK_D: rightPressed = false; break;
            case KeyEvent.VK_R: isShooting = false; break;
            case KeyEvent.VK_ENTER: enterPressed = false; break;
            case KeyEvent.VK_P: skipLevelPressed = false; break;
        }
    }
}