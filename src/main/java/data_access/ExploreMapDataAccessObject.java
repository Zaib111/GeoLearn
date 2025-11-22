package data_access;

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
import use_case.explore_map.ExploreMapDataAccessInterface;

import java.io.File;
import java.io.IOException;

/**
 * Data Access Object for the Explore Map use case.
 */
public class ExploreMapDataAccessObject implements ExploreMapDataAccessInterface {
    private SimpleFeatureSource featureSource;

    @Override
    public SimpleFeatureSource loadShapefile(String filePath) throws IOException {
        File file = new File(filePath);
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        if (store == null) {
            throw new IOException("Could not find data store for file: " + filePath);
        }
        this.featureSource = store.getFeatureSource();
        return this.featureSource;
    }

    @Override
    public SimpleFeature getFeatureAtPosition(Coordinate coordinate) {
        if (featureSource == null) {
            return null;
        }

        try {
            SimpleFeatureCollection collection = featureSource.getFeatures();
            try (SimpleFeatureIterator iterator = collection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();

                    if (geometry != null) {
                        Point point = geometry.getFactory().createPoint(coordinate);
                        if (geometry.contains(point)) {
                            return feature;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error getting feature at position: " + e.getMessage());
        }

        return null;
    }

    @Override
    public ReferencedEnvelope getMaxBounds() {
        if (featureSource == null) {
            return null;
        }
        try {
            return featureSource.getBounds();
        } catch (IOException e) {
            System.err.println("Error getting max bounds: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void setFeatureSource(SimpleFeatureSource featureSource) {
        this.featureSource = featureSource;
    }

    public SimpleFeatureSource getFeatureSource() {
        return featureSource;
    }
}
