package fi.hsl.parkandride.infrastructure.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fi.hsl.parkandride.application.domain.IdentifiedDomainObject;

public class AbstractMemoryRepository<T extends IdentifiedDomainObject> {
    private Map<Long, T> domainObjects = Collections.emptyMap();

    public T findOne(Long id) {
        return domainObjects.get(id);
    }

    public Collection<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(domainObjects.values()));
    }

    public synchronized void save(T domainObject) {
        HashMap<Long, T> mutable = new HashMap<>(domainObjects);
        mutable.put(domainObject.getId(), domainObject);
        domainObjects = Collections.unmodifiableMap(mutable);
    }

    public synchronized void delete(Long id) {
        if (domainObjects.containsKey(id)) {
            HashMap<Long, T> mutable = new HashMap<>(domainObjects);
            mutable.remove(id);
            domainObjects = Collections.unmodifiableMap(mutable);
        }
    }

    public synchronized void reset(Map<Long, T> newDomainObjects) {
        Objects.requireNonNull(newDomainObjects);
        domainObjects = Collections.unmodifiableMap(new HashMap<>(newDomainObjects));
    }
}

