package app.views.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;

/**
 * State object for the Explore Map view.
 */
public class ExploreMapState {

    /** The currently loaded map data. */
    private SimpleFeatureSource featureSource;

    /** The selected feature on the map, if any. */
    private SimpleFeature selectedFeature;

    /** The name of the selected country, if available. */
    private String selectedCountryName;

    /** The current error message to display. */
    private String errorMessage;

    /** Whether a map has been successfully loaded. */
    private boolean mapLoaded;

    /** The current interaction mode: PAN, ZOOM, or SELECT. */
    private String interactionMode;

    /**
     * Default constructor.
     * Initializes with no feature selected and map not yet loaded.
     */
    public ExploreMapState() {
        this.interactionMode = "PAN";
    }

    /**
     * Constructs a state object with all fields.
     *
     * @param featureSource the map feature source
     * @param selectedFeature the selected feature
     * @param selectedCountryName the selected country name
     * @param errorMessage the current error message
     * @param mapLoaded whether a map is loaded
     * @param interactionMode the current interaction mode
     */
    public ExploreMapState(SimpleFeatureSource featureSource,
                           SimpleFeature selectedFeature,
                           String selectedCountryName,
                           String errorMessage,
                           boolean mapLoaded,
                           String interactionMode) {
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
