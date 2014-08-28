package fi.hsl.parkandride.adapter.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.data.repository.CrudRepository;

import fi.hsl.parkandride.core.domain.IdentifiedDomainObject;

public class AbstractMemoryRepository<T extends IdentifiedDomainObject> implements CrudRepository<T, Long> {
    private Map<Long, T> domainObjects = Collections.emptyMap();

    @Override
    public  synchronized <S extends T> S save(S domainObject) {
        if (domainObject.getId() == null) {
            domainObject.setId(nextSequence());
        }

        HashMap<Long, T> mutable = new HashMap<>(domainObjects);
        mutable.put(domainObject.getId(), domainObject);
        domainObjects = Collections.unmodifiableMap(mutable);
        return domainObject;
    }

    @Override
    public T findOne(Long id) {
        return domainObjects.get(id);
    }

    @Override
    public boolean exists(Long id) {
        return domainObjects.containsKey(id);
    }

    @Override
    public Iterable<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(domainObjects.values()));
    }

    @Override
    public long count() {
        return domainObjects.size();
    }

    @Override
    public synchronized void delete(Long id) {
        if (exists(id)) {
            HashMap<Long, T> mutable = new HashMap<>(domainObjects);
            mutable.remove(id);
            domainObjects = Collections.unmodifiableMap(mutable);
        }
    }

    @Override
    public void delete(T domainObject) {
        delete(domainObject.getId());
    }

    public synchronized void reset(Map<Long, T> newDomainObjects) {
        Objects.requireNonNull(newDomainObjects);
        domainObjects = Collections.unmodifiableMap(new HashMap<>(newDomainObjects));
    }

    @Override
    public <S extends T> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<T> findAll(Iterable<Long> longs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Iterable<? extends T> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException();
    }

    private Long nextSequence() {
        Long max = domainObjects.keySet().stream().reduce(Long::max).orElse(0L);
        return new AtomicLong(max).incrementAndGet();
    }
}

