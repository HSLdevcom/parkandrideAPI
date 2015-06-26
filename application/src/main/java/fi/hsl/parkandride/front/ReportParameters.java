// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front;

import fi.hsl.parkandride.core.domain.CapacityType;
import fi.hsl.parkandride.core.domain.Usage;
import java.util.Set;

public class ReportParameters {
  public String startDate;
  public String endDate;
  public int interval;
  public Set<Long> operators;
  public Set<Long> hubs;
  public Set<Long> facilities;
  public Set<Long> regions;
  public Set<Usage> usages;
  public Set<CapacityType> capacityTypes;
}
