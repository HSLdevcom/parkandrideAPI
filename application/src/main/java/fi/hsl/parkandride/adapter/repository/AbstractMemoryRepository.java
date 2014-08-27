package fi.hsl.parkandride.adapter.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import fi.hsl.parkandride.application.domain.IdentifiedDomainObject;

public class AbstractMemoryRepository<T extends IdentifiedDomainObject> {
    private Map<Long, T> domainObjects = Collections.emptyMap();

    public T findOne(Long id) {
        return domainObjects.get(id);
    }

    public Collection<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(domainObjects.values()));
    }

    public synchronized T save(T domainObject) {
        if (domainObject.getId() == null) {
            domainObject.setId(nextSequence());
        }

        HashMap<Long, T> mutable = new HashMap<>(domainObjects);
        mutable.put(domainObject.getId(), domainObject);
        domainObjects = Collections.unmodifiableMap(mutable);
        return domainObject;
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

    private Long nextSequence() {
        Long max = domainObjects.keySet().stream().reduce(Long::max).orElse(0L);
        return new AtomicLong(max).incrementAndGet();
    }
}

