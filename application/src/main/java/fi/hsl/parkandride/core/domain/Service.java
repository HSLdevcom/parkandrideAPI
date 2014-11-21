package fi.hsl.parkandride.core.domain;

import javax.validation.constraints.NotNull;

public class Service {

    public Long id;

    @NotNull
    public MultilingualString name;

    public Long getId() {
        return id;
    }

    public MultilingualString getName() {
        return name;
    }
}
