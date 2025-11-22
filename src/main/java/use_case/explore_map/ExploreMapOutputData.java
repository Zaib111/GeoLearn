package use_case.explore_map;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Output data for the Explore Map use case.
 */
public class ExploreMapOutputData {
    private final SimpleFeature selectedFeature;
    private final SimpleFeature hoveredFeature;
    private final ReferencedEnvelope displayArea;
    private final int zoomLevel;
    private final String interactionMode;
    private final String selectedCountryName;

    public ExploreMapOutputData(SimpleFeature selectedFeature,
                                SimpleFeature hoveredFeature,
                                ReferencedEnvelope displayArea,
                                int zoomLevel,
                                String interactionMode,
                                String selectedCountryName) {
        this.selectedFeature = selectedFeature;
        this.hoveredFeature = hoveredFeature;
        this.displayArea = displayArea;
        this.zoomLevel = zoomLevel;
        this.interactionMode = interactionMode;
        this.selectedCountryName = selectedCountryName;
    }

    public SimpleFeature getSelectedFeature() {
        return selectedFeature;
    }

    public SimpleFeature getHoveredFeature() {
        return hoveredFeature;
    }

    public ReferencedEnvelope getDisplayArea() {
        return displayArea;
    }

    public int getZoomLevel() {
        return zoomLevel;
    }

    public String getInteractionMode() {
        return interactionMode;
    }

    public String getSelectedCountryName() {
        return selectedCountryName;
    }
}

