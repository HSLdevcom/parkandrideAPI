// Copyright © 2015 HSL <https://www.hsl.fi>
// This program is dual-licensed under the EUPL v1.2 and AGPLv3 licenses.

package fi.hsl.parkandride.front.geojson;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import fi.hsl.parkandride.core.domain.FacilityInfo;
import fi.hsl.parkandride.core.domain.Hub;
import fi.hsl.parkandride.core.domain.SearchResults;

public class FeatureCollection {

    public static final Function<FacilityInfo, Feature> FACILITY_TO_FEATURE = new Function<FacilityInfo, Feature>() {
        @Nullable
        @Override
        public Feature apply(@Nullable FacilityInfo facility) {
            Feature feature = new Feature();
            feature.geometry = facility.location;
            feature.id = facility.id;

            ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
            builder.put("name", facility.name);
            builder.put("status", facility.status);
            builder.put("operatorId", facility.operatorId);
            builder.put("builtCapacity", facility.builtCapacity);
            builder.put("usages", facility.usages);
            if (facility.statusDescription != null) {
                builder.put("statusDescription", facility.statusDescription);
            }
            feature.properties = builder.build();

            return feature;
        }
    };

    public static final Function<Hub, Feature> HUB_TO_FEATURE = new Function<Hub, Feature>() {
        @Nullable
        @Override
        public Feature apply(@Nullable Hub hub) {
            Feature feature = new Feature();
            feature.geometry = hub.location;
            feature.id = hub.id;
            feature.properties = ImmutableMap.of(
                "name", hub.name,
                "facilityIds", hub.facilityIds
            );
            return feature;
        }
    };

    public static FeatureCollection ofFacilities(SearchResults<FacilityInfo> searchResults) {
        return new FeatureCollection(Lists.transform(searchResults.results, FACILITY_TO_FEATURE), searchResults.hasMore);
    }

    public static FeatureCollection ofHubs(SearchResults<Hub> searchResults) {
        return new FeatureCollection(Lists.transform(searchResults.results, HUB_TO_FEATURE), searchResults.hasMore);
    }


    public final boolean hasMore;

    private final List<Feature> features;


    public FeatureCollection(List<Feature> features, boolean hasMore) {
        this.hasMore = hasMore;
        this.features = features;
    }


    public boolean isHasMore() {
        return hasMore;
    }

    public String getType() {
        return "FeatureCollection";
    }

    public List<Feature> getFeatures() {
        return features;
    }
}
