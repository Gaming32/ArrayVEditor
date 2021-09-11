package io.github.arrayvedit;

import java.awt.GridBagLayout;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;

public class EditorFrame extends JFrame {
    public EditorFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

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

        createComponents();

        pack();
        setVisible(true);
    }

    protected void createComponents() {
        

        JPanel mainPanel = new JPanel();

        GroupLayout layout = new GroupLayout(mainPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.CENTER)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
        );

        GridBagLayout gbLayout = new GridBagLayout();
        getContentPane().setLayout(gbLayout);
        mainPanel.setLayout(layout);
        add(mainPanel);
    }
}
