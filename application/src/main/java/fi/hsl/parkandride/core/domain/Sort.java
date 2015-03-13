package fi.hsl.parkandride.core.domain;

import static fi.hsl.parkandride.core.domain.Sort.Dir.ASC;

public class Sort {

    public static enum Dir { ASC, DESC }


    private String by;

    private Dir dir  = ASC;


    public Sort() {}

    public Sort(String by) {
        this.setBy(by);
    }

    public Sort(String by, Dir dir) {
        this.setBy(by);
        this.setDir(dir);
    }

    // NOTE: getters'n'setters are required for Spring GET request binding

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Dir getDir() {
        return dir;
    }

    public void setDir(Dir dir) {
        this.dir = dir;
    }
}
