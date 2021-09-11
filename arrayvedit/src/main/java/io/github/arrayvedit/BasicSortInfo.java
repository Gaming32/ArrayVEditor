package io.github.arrayvedit;

public class BasicSortInfo {
    public final String id, displayName;

    public BasicSortInfo(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String toString() {
        return displayName;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (!(o instanceof BasicSortInfo)) {
            return false;
        } else {
            BasicSortInfo other = (BasicSortInfo)o;
            return this.id.equals(other.id);
        }
    }
}
