package adapters.ExploreMap;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * State for the Explore Map view.
 */
public class ExploreMapState {
    private SimpleFeature selectedFeature;
    private SimpleFeature hoveredFeature;
    private ReferencedEnvelope displayArea;
    private int zoomLevel;
    private String interactionMode = "PAN";
    private String selectedCountryName;
    private String errorMessage;
    private boolean mapLoaded = false;

    public SimpleFeature getSelectedFeature() {
        return selectedFeature;
    }

    public void setSelectedFeature(SimpleFeature selectedFeature) {
        this.selectedFeature = selectedFeature;
    }

    public SimpleFeature getHoveredFeature() {
        return hoveredFeature;
    }

    public void setHoveredFeature(SimpleFeature hoveredFeature) {
        this.hoveredFeature = hoveredFeature;
    }

    public ReferencedEnvelope getDisplayArea() {
        return displayArea;
    }

    public void setDisplayArea(ReferencedEnvelope displayArea) {
        this.displayArea = displayArea;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(int zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    public String getInteractionMode() {
        return interactionMode;
    }

    public void setInteractionMode(String interactionMode) {
        this.interactionMode = interactionMode;
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
}

