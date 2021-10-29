package io.github.arrayvedit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class ClassUtils {
    public static JavaClass parse(File file) throws IOException {
        ClassParser parser = new ClassParser(file.getAbsolutePath());
        return parser.parse();
    }

    public static JavaClass parse(Path path) throws IOException {
        ClassParser parser = new ClassParser(Files.newInputStream(path, StandardOpenOption.READ), path.toString());
        return parser.parse();
    }
}
