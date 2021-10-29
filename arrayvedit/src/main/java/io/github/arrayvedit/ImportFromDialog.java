package io.github.arrayvedit;

import java.awt.Dialog;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.nio.file.FileSystem;

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
        super(parent, "Transfer Sorts", Dialog.ModalityType.DOCUMENT_MODAL);
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

        setTitle("Transfer Sorts");
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

        JButton transferButton = new JButton();
        transferButton.setText("Transfer");
        transferButton.addActionListener(e -> {
            int[] sortIndices = sortsList.getSelectedIndices();
            if (sortIndices.length == 0) {
                JOptionPane.showMessageDialog(this, "No sorts selected!", "Transfer Sorts", JOptionPane.ERROR_MESSAGE);
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
                .addComponent(transferButton)
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(currentlyLoaded)
                .addComponent(sortsScrollPane)
                .addComponent(transferButton)
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

    private void transferSorts(int[] sortIndices) {
        BasicSortInfo[] sorts = getSortsFromIndices(sortIndices);
        System.out.println("Transfer " + sorts.length + " sorts");
    }
}
