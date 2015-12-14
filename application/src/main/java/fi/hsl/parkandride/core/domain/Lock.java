// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

/**
 * Cluster-wide mutex lock that can be acquired via LockDao.
 */
public class Lock {
    @NotNull public final String name;
    @NotNull public final String owner;
    @NotNull public final DateTime validUntil;

    public Lock(String name, String owner, DateTime validUntil) {
        this.name = name;
        this.owner = owner;
        this.validUntil = validUntil;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Lock.class)
                .add("name", name)
                .add("owner", owner)
                .add("validUntil", validUntil)
                .toString();
    }
}
