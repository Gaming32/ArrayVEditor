package io.github.arrayvedit;

import java.util.Comparator;

import javax.swing.DefaultListModel;

public class ListModelUtils {
    public static <E extends Comparable<E>> int binarySearch(DefaultListModel<E> a, E key) {
        return binarySearch(a, 0, a.size(), key, comparableToComparator());
    }

    public static <E> int binarySearch(DefaultListModel<E> a, E key, Comparator<E> comparator) {
        return binarySearch(a, 0, a.size(), key, comparator);
    }

    public static <E extends Comparable<E>> int binarySearch(DefaultListModel<E> a, int fromIndex, int toIndex,
                                                             E key) {
        return binarySearch(a, fromIndex, toIndex, key, comparableToComparator());
    }

    public static <E> int binarySearch(DefaultListModel<E> a, int fromIndex, int toIndex,
                                       E key, Comparator<E> comparator) {
        int low = fromIndex;
        int high = toIndex - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            E midVal = a.getElementAt(mid);
            int cmp = comparator.compare(midVal, key);

            if (cmp < 0)
                low = mid + 1;
            else if (cmp > 0)
                high = mid - 1;
            else
                return mid; // key found
        }
        return -(low + 1);  // key not found.
    }

    private static <E extends Comparable<E>> Comparator<E> comparableToComparator() {
        return (a, b) -> a.compareTo(b);
    }
}
