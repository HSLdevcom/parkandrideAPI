// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.util;

import com.google.common.collect.ImmutableMap;
import fi.hsl.parkandride.core.domain.Violation;
import fi.hsl.parkandride.core.domain.validation.NotNullElement;
import fi.hsl.parkandride.core.service.ValidationException;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static fi.hsl.parkandride.core.util.Predicates.*;
import static java.util.stream.Collectors.toList;

public class ArgumentValidator<T> {
    private final T obj;
    private final Set<PredicateWithMessage<T>> predicates;

    ArgumentValidator(T obj, @Nonnull @NotNullElement Set<PredicateWithMessage<T>> predicates) {
        this.obj = obj;
        this.predicates = Objects.requireNonNull(predicates);
    }

    public static <T> Builder<T> validate(T arg) {
        return new Builder(arg);
    }

    public static <T> Builder<T> validate(T arg, String type) {
        return new Builder(arg, type);
    }

    public T match() {
        final List<Violation> violations = predicates.stream()
                .map(pred -> pred.test(obj))
                .filter(Objects::nonNull)
                .collect(toList());

        if (violations.size() > 0) {
            throw new ValidationException(violations);
        }
        return obj;
    }

    private static class PredicateWithMessage<T> {

        final Predicate<T> predicate;
        final Supplier<String> type;
        final Function<T, String> message;

        public PredicateWithMessage(Predicate<T> predicate, Supplier<String> type, Function<T, String> message) {
            this.predicate = predicate;
            this.type = type;
            this.message = message;
        }

        Violation test(T obj) {
            if (predicate.test(obj)) {
                return null;
            }
            return new Violation(type.get(), ImmutableMap.of(), null, message.apply(obj));
        }

    }

    public static class Builder<T> {

        private final T obj;
        private final Set<PredicateWithMessage<T>> predicates = new HashSet<>();
        private String type = "Unknown";

        public Builder(T obj) {
            this.obj = obj;
        }

        public Builder(T obj, String type) {
            this(obj);
            this.type = type;
        }

        public Builder<T> addPredicate(Predicate<T> pred, Function<T, String> message) {
            predicates.add(new PredicateWithMessage<>(
                    pred,
                    () -> type,
                    message
            ));
            return this;
        }

        public Builder<T> addPredicate(Predicate<T> pred, String message) {
            return addPredicate(pred, t -> message);
        }

        public T gt(Comparable<? super T> other) {
            addPredicate(
                    greaterThan(other),
                    t -> String.format("<%s> must be greater than <%s>", t, other)
            );
            return match();
        }

        public T gte(Comparable<? super T> other) {
            addPredicate(
                    greaterThanOrEqualTo(other),
                    t -> String.format("<%s> must be greater than or equal to <%s>", t, other)
            );
            return match();
        }

        public T lt(Comparable<? super T> other) {
            addPredicate(
                    lessThan(other),
                    t -> String.format("<%s> must be less than <%s>", t, other)
            );
            return match();
        }

        public T lte(Comparable<? super T> other) {
            addPredicate(
                    lessThanOrEqualTo(other),
                    t -> String.format("<%s> must be less than or equal to <%s>", t, other)
            );
            return match();
        }

        public T match() {
            return build().match();
        }

        public ArgumentValidator<T> build() {
            Objects.requireNonNull(type, "Validation type not supplied");
            return new ArgumentValidator<>(obj, predicates);
        }

    }
}
