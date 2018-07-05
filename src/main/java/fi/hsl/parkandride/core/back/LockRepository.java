// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.back;

import fi.hsl.parkandride.core.domain.Lock;
import org.joda.time.Duration;

public interface LockRepository {

    Lock acquireLock(String lockName, Duration lockDuration);

    boolean releaseLock(Lock lock);
}
