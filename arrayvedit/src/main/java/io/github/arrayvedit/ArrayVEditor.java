package io.github.arrayvedit;

import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;

public class ArrayVEditor {
    public static void main(String[] args) {
        final OsThemeDetector detector = OsThemeDetector.getDetector();
        final boolean isDarkThemeUsed = detector.isDark();
        if (isDarkThemeUsed) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        detector.registerListener(isDark -> {
            SwingUtilities.invokeLater(() -> {
                if (isDark) {
                    FlatDarkLaf.setup();
                } else {
                    FlatLightLaf.setup();
                }
            });
        });
        SwingUtilities.invokeLater(() -> new EditorFrame());
    }
}
