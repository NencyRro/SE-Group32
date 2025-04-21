package com.feedback.util;

import java.io.*;
import java.nio.file.*;
import javax.swing.JOptionPane;

public class FileUtil {
    public static boolean saveToFile(String fileName, String content) {
        try {
            // Create directories if they don't exist
            Path path = Paths.get(fileName);
            Files.createDirectories(path.getParent());

            // Append content to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.write(content);
                writer.newLine();
                return true;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error saving to file: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static String readFromFile(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                return "No content available.";
            }
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }
}