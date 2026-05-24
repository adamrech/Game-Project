// FontManager.java
package org.example;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class FontManager {
    public static Font loadFont(String filePath, float size) {
        try {
            File fontFile = new File(filePath);
            if (fontFile.exists()) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
                return customFont.deriveFont(size);
            } else {
                System.out.println("Cannot find font file: " + filePath);
            }
        } catch (FontFormatException | IOException e) {
            System.out.println("Error loading font: " + e.getMessage());
        }
        return new Font("Consolas", Font.BOLD, (int)size);
    }
}