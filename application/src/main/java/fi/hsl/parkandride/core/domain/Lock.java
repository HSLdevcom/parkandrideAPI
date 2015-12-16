// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.Objects;

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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Lock) {
            Lock other = (Lock) obj;
            return Objects.equals(this.name, other.name)
                    && Objects.equals(this.owner, other.owner)
                    && Objects.equals(this.validUntil, other.validUntil);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (validUntil != null ? validUntil.hashCode() : 0);
        return result;
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
