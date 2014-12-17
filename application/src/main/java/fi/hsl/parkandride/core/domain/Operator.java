package fi.hsl.parkandride.core.domain;

public class Operator {

    public Long id;

    public MultilingualString name;

    public Operator() {}

    public Operator(String name) {
        this.name = new MultilingualString(name);
    }
}
