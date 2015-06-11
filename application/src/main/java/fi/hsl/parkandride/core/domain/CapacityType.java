// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.core.domain;

import static java.util.Arrays.asList;

import java.util.List;

public enum CapacityType {
    CAR,
    DISABLED,
    ELECTRIC_CAR,
    MOTORCYCLE,
    BICYCLE,
    BICYCLE_SECURE_SPACE;

    public static final CapacityType[] motorCapacities = new CapacityType[]{CAR, ELECTRIC_CAR, MOTORCYCLE};
    public static final CapacityType[] bicycleCapacities = new CapacityType[]{BICYCLE, BICYCLE_SECURE_SPACE};
    public static final List<CapacityType> motorCapacityList = asList(motorCapacities);
    public static final List<CapacityType> bicycleCapacityList = asList(bicycleCapacities);
}
