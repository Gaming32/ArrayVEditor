package io.github.arrayvedit;

import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;

public class ImportFromDialog extends JDialog {
    protected final EditorFrame parent;
    protected final OsThemeDetector themeDetector;

    protected JLabel currentlyLoaded;
    protected DefaultListModel<BasicSortInfo> sortsListModel;
    protected JList<BasicSortInfo> sortsList;

    protected FileSystem jarFs;

    public ImportFromDialog(EditorFrame parent, OsThemeDetector themeDetector) {
        super(parent, "Copy Sorts", Dialog.ModalityType.DOCUMENT_MODAL);
        this.parent = parent;

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

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        createComponents();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (jarFs != null) {
                    try {
                        jarFs.close();
                    } catch (IOException exc) {
                        parent.showErrorMessage(exc);
                    }
                }
            }
        });
    }

    protected void createComponents() {
        currentlyLoaded = new JLabel();

        sortsListModel = new DefaultListModel<>();

        sortsList = new JList<>();
        sortsList.setEnabled(false);
        sortsList.setModel(sortsListModel);

        JScrollPane sortsScrollPane = new JScrollPane();
        sortsScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sortsScrollPane.setViewportView(sortsList);

        JButton copyButton = new JButton();
        copyButton.setText("Copy");
        copyButton.addActionListener(e -> {
            int[] sortIndices = sortsList.getSelectedIndices();
            if (sortIndices.length == 0) {
                JOptionPane.showMessageDialog(this, "No sorts selected!", "Copy Sorts", JOptionPane.ERROR_MESSAGE);
                return;
            }
            transferSorts(sortIndices);
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.CENTER)
                .addComponent(currentlyLoaded)
                .addComponent(sortsScrollPane)
                .addComponent(copyButton)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(currentlyLoaded)
                .addComponent(sortsScrollPane)
                .addComponent(copyButton)
        );
    }

    protected BasicSortInfo[] getSortsFromIndices(int[] sortIndices) {
        BasicSortInfo[] sorts = new BasicSortInfo[sortIndices.length];
        int i = 0;
        for (int sortIndex : sortIndices) {
            sorts[i++] = sortsListModel.get(sortIndex);
        }
        return sorts;
    }

    protected void transferSorts(int[] sortIndices) {
        BasicSortInfo[] sorts = getSortsFromIndices(sortIndices);
        int count = 0;
        for (Path fullPath : EditorFrame.getSortPaths(jarFs, sorts)) {
            String fullName = fullPath.toString();
            String className = fullName.substring(0, fullName.length() - ".class".length());
            if (!transferSort(fullPath)) continue;
            PathMatcher matcher = jarFs.getPathMatcher("glob:" + className + "$*.class");
            Iterable<Path> siblings = () -> {
                try {
                    return Files.list(fullPath.getParent()).iterator();
                } catch (IOException e) {
                    return new Iterator<Path>() {
                        @Override
                        public boolean hasNext() {
                            return false;
                        }
                        @Override
                        public Path next() {
                            throw new NoSuchElementException();
                        }
                    };
                }
            };
            for (Path sibling : siblings) {
                if (matcher.matches(sibling)) {
                    transferSort(sibling);
                }
            }
            count++;
        }

        if (count > 0) {
            JOptionPane.showMessageDialog(this, sorts.length + " sort(s) copied", "Copy Sorts", count < sorts.length ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }
    }

    protected boolean transferSort(Path path) {
        boolean success;
        try {
            success = parent.importFromClassFile(ClassUtils.parse(path), path);
        } catch (IOException e) {
            parent.showErrorMessage(e, "Copy Sort");
            success = false;
        }
        return success;
    }
}
