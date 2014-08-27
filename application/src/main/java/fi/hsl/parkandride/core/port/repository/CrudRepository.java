package fi.hsl.parkandride.core.port.repository;

import java.util.Collection;

import fi.hsl.parkandride.core.domain.IdentifiedDomainObject;

public interface CrudRepository<T extends IdentifiedDomainObject> {
    Collection<T> findAll();

    T findOne(Long id);

    void delete(Long id);

    T save(T parkingArea);
}
