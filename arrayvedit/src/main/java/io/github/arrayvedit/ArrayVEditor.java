package io.github.arrayvedit;

import javax.swing.SwingUtilities;

public class ArrayVEditor {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditorFrame());
    }
}
