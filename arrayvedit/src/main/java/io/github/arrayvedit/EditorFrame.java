package io.github.arrayvedit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jthemedetecor.OsThemeDetector;

import org.apache.bcel.classfile.JavaClass;

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

        fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        createComponents();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (jarFs != null) {
                    try {
                        jarFs.close();
                    } catch (IOException exc) {
                        showErrorMessage(exc);
                        return;
                    }
                }
            }
        });

        pack();
        setTitle("ArrayVEditor");
        setVisible(true);
    }

    public void showErrorMessage(Throwable e, String title) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(EditorFrame.this, e.toString(), title, JOptionPane.ERROR_MESSAGE);
    }

    public void showErrorMessage(Throwable e) {
        showErrorMessage(e, getTitle());
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
            if (jarFs == null) {
                JOptionPane.showMessageDialog(this, "No JAR opened!", "Import Sorts", JOptionPane.ERROR_MESSAGE);
                return;
            }
            setupImportChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File[] files = fileChooser.getSelectedFiles();
                importSorts(files);
            }
        });

        JButton exportButton = new JButton();
        exportButton.setText("Export...");
        exportButton.addActionListener(e -> {
            int[] sortIndices = sortsList.getSelectedIndices();
            if (sortIndices.length == 0) {
                JOptionPane.showMessageDialog(this, "No sorts selected!", "Export Sorts", JOptionPane.ERROR_MESSAGE);
                return;
            }
            setupExportChooser();
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                int shouldDecompile = JOptionPane.showConfirmDialog(this, "Would you like to decompile the sort to a .java file?", "Export Sorts", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (shouldDecompile == JOptionPane.CANCEL_OPTION) return;
                File file = fileChooser.getSelectedFile();
                exportSorts(file, sortIndices, shouldDecompile == JOptionPane.YES_OPTION);
            }
        });

        JButton deleteButton = new JButton();
        deleteButton.setText("Delete");
        deleteButton.addActionListener(e -> {
            int[] sortIndices = sortsList.getSelectedIndices();
            if (sortIndices.length == 0) {
                JOptionPane.showMessageDialog(this, "No sorts selected!", "Delete Sorts", JOptionPane.ERROR_MESSAGE);
                return;
            }
            deleteSorts(sortIndices);
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
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.resetChoosableFileFilters();
        fileChooser.addChoosableFileFilter(FileFilters.JAVA_ARCHIVE);
    }

    protected void setupImportChooser() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.resetChoosableFileFilters();
        fileChooser.addChoosableFileFilter(
            FileFilters.mergeExtensionFilters("All supported (*.class)",
            FileFilters.JAVA_CLASS
        ));
        // fileChooser.addChoosableFileFilter(FileFilters.JAVA_ARCHIVE);
        fileChooser.addChoosableFileFilter(FileFilters.JAVA_CLASS);
        // fileChooser.addChoosableFileFilter(FileFilters.JAVA_SOURCE);
    }

    protected void setupExportChooser() {
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.resetChoosableFileFilters();
    }

    protected void loadJar(File file) {
        try {
            jarFs = FileSystems.newFileSystem(file.toPath(), (ClassLoader)null);
        } catch (IOException exc) {
            showErrorMessage(exc, "Open JAR");
            return;
        }
        currentlyLoaded.setText(file.getName());

        List<BasicSortInfo> sorts = new ArrayList<>();
        Path sortsDir = jarFs.getPath("sorts");
        PathMatcher isClass = jarFs.getPathMatcher("glob:**.class");
        try {
            Files.walk(sortsDir).filter(p -> isClass.matches(p)).forEach(path -> {
                Path relativePath = sortsDir.relativize(path);
                String pathStr = relativePath.toString();
                String className = pathStr.substring(0, pathStr.length() - ".class".length());
                BasicSortInfo sort = new BasicSortInfo(className, className);
                sorts.add(sort);
            });
        } catch (IOException e) {
            showErrorMessage(e, "Open JAR");
            return;
        }

        if (sorts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "This JAR doesn't appear to have any sorts. Please verifiy that this is an ArrayV JAR.", "Open JAR", JOptionPane.WARNING_MESSAGE);
        }

        Collections.sort(sorts);
        sortsListModel.clear();
        sortsListModel.addAll(sorts);

        sortsList.setEnabled(true);
        pack();
    }

    protected BasicSortInfo[] getSortsFromIndices(int[] sortIndices) {
        BasicSortInfo[] sorts = new BasicSortInfo[sortIndices.length];
        int i = 0;
        for (int sortIndex : sortIndices) {
            sorts[i++] = sortsListModel.get(sortIndex);
        }
        return sorts;
    }

    protected void importSorts(File[] files) {
        int success = (int)Arrays.stream(files).map(this::importSort).filter(b -> b).count();
        JOptionPane.showMessageDialog(this,
            "Successfully imported " + success + " sort(s)",
            "Import Sorts", JOptionPane.INFORMATION_MESSAGE);
    }

    protected boolean importSort(File file) {
        String ext = Utils.getExt(file);

        switch (ext.toLowerCase()) {
            case ".class":
                JavaClass classInfo;
                try {
                    classInfo = ClassUtils.parse(file);
                } catch (IOException e) {
                    showErrorMessage(e, "Import Sort");
                    return false;
                }
                String packageName = classInfo.getPackageName();
                if (!packageName.startsWith("sorts.")) {
                    int shouldImport = JOptionPane.showConfirmDialog(this,
                        "The sort \"" + classInfo.getClassName() + "\" doesn't appear to be a sort. Would you like to import it anyway?",
                        "Import Sort", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (shouldImport != JOptionPane.YES_OPTION) {
                        return false;
                    }
                }
                String[] sortPath = classInfo.getClassName().split("\\.");
                sortPath[sortPath.length - 1] += ".class";
                Path destination = jarFs.getPath(sortPath[0], Arrays.copyOfRange(sortPath, 1, sortPath.length));
                if (Files.exists(destination)) {
                    int shouldImport = JOptionPane.showConfirmDialog(this,
                        "The sort \"" + classInfo.getClassName() + "\" seems to already be in the JAR. Would you like to import it anyway?",
                        "Import Sort", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (shouldImport != JOptionPane.YES_OPTION) {
                        return false;
                    }
                }
                try {
                    Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    showErrorMessage(e, "Import Sort");
                    return false;
                }
                String name = Arrays.stream(Arrays.copyOfRange(sortPath, 1, sortPath.length)).collect(Collectors.joining("/"));
                name = name.substring(0, name.length() - ".class".length());
                BasicSortInfo info = new BasicSortInfo(name, name);
                sortsListModel.insertElementAt(info, Math.abs(ListModelUtils.binarySearch(sortsListModel, info)) - 1);
                return true;

            default:
                JOptionPane.showMessageDialog(this,
                    "Unable to load \"" + ext + "\" files.",
                    "Import Sort", JOptionPane.ERROR_MESSAGE);
                return false;
        }
    }

    protected void exportSorts(File dir, int[] sortIndices, boolean decompile) {
        BasicSortInfo[] sorts = getSortsFromIndices(sortIndices);

        if (decompile) {
            int shouldCancel = JOptionPane.showConfirmDialog(this, "Decompiling not yet supported. Would you like to continue without decompiling?", "Export Sorts", JOptionPane.YES_NO_OPTION);
            if (shouldCancel == JOptionPane.NO_OPTION) return;
        }

        int count = 0;
        Path dirPath = dir.toPath();
        try {
            for (Path fullPath : getSortPaths(sorts)) {
                String fileName = fullPath.getFileName().toString();
                Files.copy(fullPath, dirPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                count++;
            }
        } catch (IOException e) {
            showErrorMessage(e, "Export Sorts");
        }

        if (count > 0) {
            JOptionPane.showMessageDialog(this, sorts.length + " sort(s) exported", "Export Sorts", count < sorts.length ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }
    }

    protected void deleteSorts(int[] sortIndices) {
        BasicSortInfo[] sorts = getSortsFromIndices(sortIndices);

        String sortMessage = Arrays.stream(sorts)
            .map(BasicSortInfo::toString)
            .collect(Collectors.joining(", "));

        int dialogResult = JOptionPane.showConfirmDialog(this, 
            String.format(
                "<html>Are you sure you would like to delete %1$s sort(s)?<br>" +
                "%2$s<br>" +
                "<b>THIS ACTION IS IRREVERSIBLE!</b></html>",
                sorts.length, sortMessage
            ),
            "Delete Sorts", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (dialogResult != JOptionPane.YES_OPTION) return;

        int i = 0;
        for (int sortIndex : sortIndices) {
            sortsListModel.remove(sortIndex - (i++));
        }

        int count = 0;
        try {
            for (Path fullPath : getSortPaths(sorts)) {
                Files.delete(fullPath);
                count++;
            }
        } catch (IOException e) {
            showErrorMessage(e, "Delete Sorts");
            return;
        }

        if (count > 0) {
            JOptionPane.showMessageDialog(this, sorts.length + " sort(s) deleted", "Delete Sorts", count < sorts.length ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public Iterable<Path> getSortPaths(BasicSortInfo[] sorts) {
        return () -> Arrays.stream(sorts).map(sort -> jarFs.getPath("sorts", sort.id + ".class")).iterator();
    }
}
