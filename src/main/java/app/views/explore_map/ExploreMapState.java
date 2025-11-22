package app.views.explore_map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;

/**
 * State for the Explore Map view.
 * This stores all the state for the map exploration feature.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExploreMapState {
    private SimpleFeatureSource featureSource;
    private SimpleFeature selectedFeature;
    private String selectedCountryName;
    private String errorMessage;
    private boolean mapLoaded = false;
    private String interactionMode = "PAN"; // PAN, ZOOM, SELECT
}
