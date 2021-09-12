package io.github.arrayvedit;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.jetbrains.java.decompiler.main.decompiler.PrintStreamLogger;

public class DecompilerUtils {
    public static void decompile(File from, File to) {
        decompile(new File[] {from}, to, new HashMap<>() {});
    }

    public static void decompile(File[] sources, File to) {
        decompile(sources, to, new HashMap<>() {});
    }

    public static void decompile(File from, File to, Map<String, Object> options) {
        decompile(new File[] {from}, to, options);
    }

    public static void decompile(File[] sources, File to, Map<String, Object> options) {
        LibraryDecompiler decompiler = new LibraryDecompiler(to, options);
        Arrays.stream(sources).forEach(decompiler::addSource);
        decompiler.decompileContext();
    }

    // Publicize constructor
    protected static class LibraryDecompiler extends ConsoleDecompiler {
        public LibraryDecompiler(File destination, Map<String, Object> options) {
            super(destination, options, new PrintStreamLogger(System.out));
        }
    }
}
