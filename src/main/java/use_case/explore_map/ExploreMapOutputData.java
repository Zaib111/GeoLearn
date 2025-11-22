package use_case.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;

/**
 * Output data for the Explore Map use case.
 * Simplified to only include data from backend operations.
 */
public class ExploreMapOutputData {
    private final SimpleFeatureSource featureSource;
    private final SimpleFeature selectedFeature;
    private final String selectedCountryName;

    public ExploreMapOutputData(SimpleFeatureSource featureSource,
                                SimpleFeature selectedFeature,
                                String selectedCountryName) {
        this.featureSource = featureSource;
        this.selectedFeature = selectedFeature;
        this.selectedCountryName = selectedCountryName;
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    public SimpleFeature getSelectedFeature() {
        return selectedFeature;
    }

    public String getSelectedCountryName() {
        return selectedCountryName;
    }
}
