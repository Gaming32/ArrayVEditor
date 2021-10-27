package io.github.arrayvedit;

import java.io.File;

public class Utils {
    public static String getExtless(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String getExtless(File file) {
        return getExtless(file.getPath());
    }

    public static String getExt(String name) {
        return name.substring(name.lastIndexOf("."));
    }

    public static String getExt(File file) {
        return getExt(file.getName());
    }
}
