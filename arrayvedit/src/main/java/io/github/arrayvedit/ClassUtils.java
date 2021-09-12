package io.github.arrayvedit;

import java.io.File;
import java.io.IOException;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

public class ClassUtils {
    public static JavaClass parse(File file) throws IOException {
        ClassParser parser = new ClassParser(file.getAbsolutePath());
        return parser.parse();
    }
}
