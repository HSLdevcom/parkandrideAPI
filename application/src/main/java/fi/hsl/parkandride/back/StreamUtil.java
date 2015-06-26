// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.back;

import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {

    public static <T> Stream<T> asStream(Optional<T> optional) {
        return optional.map(Stream::of).orElse(Stream.empty());
    }

    public static <T> Stream<T> asStream(Iterator<T> iterator) {
        return asStream(iterator, false);
    }

    public static <T> Stream<T> asStream(Iterator<T> iterator, boolean parallel) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }
}
