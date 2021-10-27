package io.github.arrayvedit;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

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

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> ArrayList<E> asArrayList(E... a) {
        return new ArrayList<>(Arrays.asList(a));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <E> Deque<E> asDeque(E... a) {
        return new LinkedList<>(Arrays.asList(a));
    }
}
