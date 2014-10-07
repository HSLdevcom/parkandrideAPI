package fi.hsl.parkandride.inbound;

import java.util.Collection;

import javax.annotation.Nullable;

import org.geolatte.common.Feature;
import org.geolatte.common.dataformats.json.jackson.DefaultFeatureCollection;
import org.geolatte.common.reflection.ObjectToFeatureTransformation;
import org.geolatte.common.transformer.TransformationException;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fi.hsl.parkandride.core.domain.SearchResults;

public class FeatureResults extends DefaultFeatureCollection {

    private static final ObjectToFeatureTransformation TRANSFORMATION = new ObjectToFeatureTransformation();

    private static final Function<Object, Feature> TO_FEATURE = new Function<Object, Feature>() {
        @Nullable
        @Override
        public Feature apply(@Nullable Object input) {
            try {
                return TRANSFORMATION.transform(input);
            } catch (TransformationException e) {
                throw new RuntimeException(e);
            }
        }
    };

    public static <T> FeatureResults of(SearchResults<T> searchResults) {
        return new FeatureResults(Lists.transform(searchResults.results, TO_FEATURE), searchResults.hasMore);
    }


    public final boolean hasMore;

    public FeatureResults(Collection<Feature> features, boolean hasMore) {
        super(features);
        this.hasMore = hasMore;
    }


    public boolean isHasMore() {
        return hasMore;
    }

    public String getType() {
        return "FeatureCollection";
    }
}
