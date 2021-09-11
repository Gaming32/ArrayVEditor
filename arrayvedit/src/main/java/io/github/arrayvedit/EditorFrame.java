package io.github.arrayvedit;

import java.awt.GridBagLayout;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EditorFrame extends JFrame {
    public EditorFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createComponents();

        pack();
        setResizable(false);
        setVisible(true);
    }

    protected void createComponents() {
        JLabel currentlyLoaded = new JLabel();
        currentlyLoaded.setText("<No JAR Loaded>");

        JButton loadJARButton = new JButton();
        loadJARButton.setText("Load JAR");
        loadJARButton.addActionListener(e -> {
            
        });

        JPanel mainPanel = new JPanel();

        GroupLayout layout = new GroupLayout(mainPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.CENTER)
                .addComponent(currentlyLoaded)
                .addComponent(loadJARButton)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(currentlyLoaded)
                .addComponent(loadJARButton)
        );

        GridBagLayout gbLayout = new GridBagLayout();
        getContentPane().setLayout(gbLayout);
        mainPanel.setLayout(layout);
        add(mainPanel);
    }
}
