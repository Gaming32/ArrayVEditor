package io.github.arrayvedit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

public class FileFilters {
    public static final FileNameExtensionFilter JAVA_ARCHIVE = new FileNameExtensionFilter("Java Archive Files (*.jar)", "jar");
    public static final FileNameExtensionFilter JAVA_CLASS = new FileNameExtensionFilter("Java Class Files (*.class)", "class");
    public static final FileNameExtensionFilter JAVA_SOURCE = new FileNameExtensionFilter("Java Source Files (*.java)", "java");

    public static FileNameExtensionFilter mergeExtensionFilters(String description, FileNameExtensionFilter... filters) {
        List<String> extensions = new ArrayList<>();
        Arrays.stream(filters).forEach(filter -> extensions.addAll(Arrays.asList(filter.getExtensions())));
        return new FileNameExtensionFilter(description, extensions.toArray(new String[extensions.size()]));
    }
}
