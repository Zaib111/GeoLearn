package app.data_access;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import app.use_cases.explore_map.ExploreMapDataAccessInterface;

/**
 * Data Access Object for the Explore Map use case.
 *
 * <p>
 * Provides methods to load shapefiles and to query features and bounds
 * from the currently loaded feature source.
 */
public class ExploreMapDataAccessObject implements ExploreMapDataAccessInterface {
    private static final Logger LOGGER = Logger.getLogger(ExploreMapDataAccessObject.class.getName());

    private SimpleFeatureSource featureSource;

    @Override
    public SimpleFeatureSource loadShapefile(String filePath) throws IOException {
        final File file = new File(filePath);
        final FileDataStore store = FileDataStoreFinder.getDataStore(file);
        if (store == null) {
            throw new IOException("Could not find data store for file: " + filePath);
        }
        this.featureSource = store.getFeatureSource();
        return this.featureSource;
    }

    @Override
    public SimpleFeature getFeatureAtPosition(Coordinate coordinate) {
        SimpleFeature foundFeature = null;
        if (featureSource != null) {
            try {
                final SimpleFeatureCollection collection = featureSource.getFeatures();
                try (SimpleFeatureIterator iterator = collection.features()) {
                    while (iterator.hasNext()) {
                        final SimpleFeature feature = iterator.next();
                        final Geometry geometry = (Geometry) feature.getDefaultGeometry();

                        if (geometry != null) {
                            final Point point = geometry.getFactory().createPoint(coordinate);
                            if (geometry.contains(point)) {
                                foundFeature = feature;
                                break;
                            }
                        }
                    }
                }
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error getting feature at position: {0}", new Object[]{ex.getMessage()});
                LOGGER.log(Level.SEVERE, "Exception while getting feature at position", ex);
            }
        }

        return foundFeature;
    }

    @Override
    public ReferencedEnvelope getMaxBounds() {
        ReferencedEnvelope bounds = null;
        if (featureSource != null) {
            try {
                bounds = featureSource.getBounds();
            }
            catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Error getting max bounds: {0}", new Object[]{ex.getMessage()});
                LOGGER.log(Level.SEVERE, "Exception while getting max bounds", ex);
            }
        }
        return bounds;
    }

    @Override
    public void setFeatureSource(SimpleFeatureSource featureSource) {
        this.featureSource = featureSource;
    }

    /**
     * Returns the underlying feature source.
     * Kept for consumers/tests even if not referenced directly in the codebase.
     *
     * @return the current SimpleFeatureSource, or null if none is loaded
     */
    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }
}
