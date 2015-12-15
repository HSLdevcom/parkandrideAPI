// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Lock;
import org.joda.time.Duration;

import java.util.Optional;

public interface LockRepository {

    public Optional<Lock> acquireLock(String lockName, Duration lockDuration);

    public void releaseLock(Lock lock);
}
