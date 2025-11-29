package app.use_cases.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;

/**
 * Output data for the Explore Map use case.
 */
public class ExploreMapOutputData {

    /** The loaded map feature source, if available. */
    private final SimpleFeatureSource featureSource;

    /** The feature selected by the user, if any. */
    private final SimpleFeature selectedFeature;

    /** The name of the selected country, if available. */
    private final String selectedCountryName;

    /**
     * Constructs output data for map loading or feature selection results.
     *
     * @param featureSource the map data source, or null if unchanged
     * @param selectedFeature the selected feature, or null if none
     * @param selectedCountryName the feature's country name, or null if not applicable
     */
    public ExploreMapOutputData(SimpleFeatureSource featureSource,
                                SimpleFeature selectedFeature,
                                String selectedCountryName) {
        this.featureSource = featureSource;
        this.selectedFeature = selectedFeature;
        this.selectedCountryName = selectedCountryName;
    }

    /**
     * @return the current feature source
     */
    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    /**
     * @return the selected feature, or null if none
     */
    public SimpleFeature getSelectedFeature() {
        return selectedFeature;
    }

    /**
     * @return the selected country name, or null if none
     */
    public String getSelectedCountryName() {
        return selectedCountryName;
    }
}
