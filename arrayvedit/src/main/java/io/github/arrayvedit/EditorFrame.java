package io.github.arrayvedit;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.GroupLayout.Alignment;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class EditorFrame extends JFrame {
    protected final OsThemeDetector themeDetector;
    protected final JFileChooser fileChooser;

    protected JLabel currentlyLoaded;
    protected DefaultListModel<BasicSortInfo> sortsListModel;
    protected JList<BasicSortInfo> sortsList;

    protected FileSystem jarFs;

    public EditorFrame(OsThemeDetector themeDetector) {
        this.themeDetector = themeDetector;
        themeDetector.registerListener(isDark -> {
            SwingUtilities.invokeLater(() -> {
                if (isDark) {
                    FlatDarkLaf.setup();
                } else {
                    FlatLightLaf.setup();
                }
                SwingUtilities.updateComponentTreeUI(this);
            });
        });

        fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        createComponents();

        pack();
        // setResizable(false);
        setVisible(true);
    }

    protected void createComponents() {
        currentlyLoaded = new JLabel();
        currentlyLoaded.setText("<No JAR Loaded>");

        JButton loadJARButton = new JButton();
        loadJARButton.setText("Load JAR");
        loadJARButton.addActionListener(e -> {
            setupJarChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                loadJar(file);
            }
        });

        sortsListModel = new DefaultListModel<>();

        sortsList = new JList<>();
        sortsList.setEnabled(false);
        sortsList.setModel(sortsListModel);

        JScrollPane sortsScrollPane = new JScrollPane();
        sortsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sortsScrollPane.setViewportView(sortsList);

        JButton importButton = new JButton();
        importButton.setText("Import...");
        importButton.addActionListener(e -> {
            // TODO: Import button
        });

        JButton exportButton = new JButton();
        exportButton.setText("Export...");
        exportButton.addActionListener(e -> {
            // TODO: Export button
        });

        JButton deleteButton = new JButton();
        deleteButton.setText("Delete");
        deleteButton.addActionListener(e -> {
            // TODO: Delete button
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.CENTER)
                .addComponent(currentlyLoaded)
                .addComponent(loadJARButton)
                .addComponent(sortsScrollPane)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(importButton)
                    .addComponent(exportButton)
                    .addComponent(deleteButton)
                )
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(currentlyLoaded)
                .addComponent(loadJARButton)
                .addComponent(sortsScrollPane)
                .addGroup(layout.createParallelGroup(Alignment.CENTER)
                    .addComponent(importButton)
                    .addComponent(exportButton)
                    .addComponent(deleteButton)
                )
        );
    }

    protected void setupJarChooser() {
        fileChooser.resetChoosableFileFilters();
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Java Archive Files (.jar)", "jar"));
    }

    protected void loadJar(File file) {
        try {
            jarFs = FileSystems.newFileSystem(file.toPath());
        } catch (IOException exc) {
            exc.printStackTrace();
            JOptionPane.showMessageDialog(this, exc.toString(), "Open JAR", JOptionPane.ERROR_MESSAGE);
            return;
        }
        currentlyLoaded.setText(file.getName());

        sortsListModel.clear();
        Path sortsDir = jarFs.getPath("sorts");
        PathMatcher isClass = jarFs.getPathMatcher("glob:**.class");
        try {
            Files.walk(sortsDir).filter(p -> isClass.matches(p)).forEach(path -> {
                Path relativePath = sortsDir.relativize(path);
                String pathStr = relativePath.toString();
                String className = pathStr.substring(0, pathStr.length() - ".class".length());
                BasicSortInfo sort = new BasicSortInfo(className, className);
                sortsListModel.addElement(sort);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (sortsListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This JAR doesn't appear to have any sorts. Please verifiy that this is an ArrayV JAR.", "Open JAR", JOptionPane.WARNING_MESSAGE);
        }

        sortsList.setEnabled(true);
        pack();
    }
}
