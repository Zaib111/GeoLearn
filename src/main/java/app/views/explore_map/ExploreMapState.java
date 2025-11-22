package app.views.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;

/**
 * State for the Explore Map view.
 * This stores all the state for the map exploration feature.
 */
public class ExploreMapState {
    private SimpleFeatureSource featureSource;
    private SimpleFeature selectedFeature;
    private String selectedCountryName;
    private String errorMessage;
    private boolean mapLoaded = false;
    private String interactionMode = "PAN"; // PAN, ZOOM, SELECT

    /**
     * Default constructor.
     */
    public ExploreMapState() {
    }

    /**
     * Constructor with all fields.
     */
    public ExploreMapState(SimpleFeatureSource featureSource, SimpleFeature selectedFeature,
                          String selectedCountryName, String errorMessage,
                          boolean mapLoaded, String interactionMode) {
        this.featureSource = featureSource;
        this.selectedFeature = selectedFeature;
        this.selectedCountryName = selectedCountryName;
        this.errorMessage = errorMessage;
        this.mapLoaded = mapLoaded;
        this.interactionMode = interactionMode;
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }

    public void setFeatureSource(SimpleFeatureSource featureSource) {
        this.featureSource = featureSource;
    }

    public SimpleFeature getSelectedFeature() {
        return selectedFeature;
    }

    public void setSelectedFeature(SimpleFeature selectedFeature) {
        this.selectedFeature = selectedFeature;
    }

    public String getSelectedCountryName() {
        return selectedCountryName;
    }

    public void setSelectedCountryName(String selectedCountryName) {
        this.selectedCountryName = selectedCountryName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isMapLoaded() {
        return mapLoaded;
    }

    public void setMapLoaded(boolean mapLoaded) {
        this.mapLoaded = mapLoaded;
    }

    public String getInteractionMode() {
        return interactionMode;
    }

    public void setInteractionMode(String interactionMode) {
        this.interactionMode = interactionMode;
    }
}
