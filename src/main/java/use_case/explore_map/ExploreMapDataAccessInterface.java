package use_case.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;

/**
 * Data Access Interface for the Explore Map use case.
 */
public interface ExploreMapDataAccessInterface {
    /**
     * Load shapefile from the given path.
     * @param filePath the path to the shapefile
     * @return the feature source
     * @throws IOException if the file cannot be loaded
     */
    SimpleFeatureSource loadShapefile(String filePath) throws IOException;

    /**
     * Get the feature at the given coordinate.
     * @param coordinate the coordinate to check
     * @return the feature at that position, or null if none
     */
    SimpleFeature getFeatureAtPosition(Coordinate coordinate);

    /**
     * Get the maximum bounds of the current map.
     * @return the maximum bounds
     */
    ReferencedEnvelope getMaxBounds();

    /**
     * Set the current feature source.
     * @param featureSource the feature source
     */
    void setFeatureSource(SimpleFeatureSource featureSource);
}

