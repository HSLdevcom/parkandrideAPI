package fi.hsl.parkandride.application.port.repository;

import java.util.Collection;

import fi.hsl.parkandride.application.domain.IdentifiedDomainObject;

public interface CrudRepository<T extends IdentifiedDomainObject> {
    Collection<T> findAll();

    T findOne(Long id);

    void delete(Long id);

    T save(T parkingArea);
}
