package app.use_cases.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExploreMapInteractor to achieve 100% code coverage.
 * Tests cover: constructor, loadMap success, loadMap failure (IOException),
 * selectFeature with NAME attribute, selectFeature without NAME attribute (uses ID),
 * and selectFeature when no feature is found (null).
 */
public class ExploreMapInteractorTest {

    private TestDataAccess dataAccess;
    private TestPresenter presenter;
    private ExploreMapInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new TestDataAccess();
        presenter = new TestPresenter();
        interactor = new ExploreMapInteractor(dataAccess, presenter);
    }

    @Test
    void testConstructor() {
        // Verify that the constructor properly initializes the interactor
        assertNotNull(interactor);
    }

    @Test
    void testLoadMapSuccess() {
        // Arrange
        String shapefilePath = "\"C:\\Users\\marcu\\Desktop\\csc207\\110mm\\ne_110m_admin_0_countries.shp\"";
        ExploreMapInputData inputData = new ExploreMapInputData(shapefilePath);
        dataAccess.setShouldThrowException(false);

        // Act
        interactor.loadMap(inputData);

        // Assert
        assertTrue(dataAccess.loadShapefileWasCalled);
        assertEquals(shapefilePath, dataAccess.lastShapefilePath);
        assertTrue(dataAccess.setFeatureSourceWasCalled);
        assertTrue(presenter.prepareMapLoadedViewWasCalled);
        assertFalse(presenter.prepareFailViewWasCalled);
        assertNotNull(presenter.lastOutputData);
        assertNotNull(presenter.lastOutputData.getFeatureSource());
        assertNull(presenter.lastOutputData.getSelectedFeature());
        assertNull(presenter.lastOutputData.getSelectedCountryName());
    }

    @Test
    void testLoadMapIOException() {
        // Arrange
        String shapefilePath = "invalid/path/shapefile.shp";
        ExploreMapInputData inputData = new ExploreMapInputData(shapefilePath);
        dataAccess.setShouldThrowException(true);

        // Act
        interactor.loadMap(inputData);

        // Assert
        assertTrue(dataAccess.loadShapefileWasCalled);
        assertEquals(shapefilePath, dataAccess.lastShapefilePath);
        assertFalse(dataAccess.setFeatureSourceWasCalled);
        assertTrue(presenter.prepareFailViewWasCalled);
        assertFalse(presenter.prepareMapLoadedViewWasCalled);
        assertTrue(presenter.lastErrorMessage.contains("Error loading map"));
    }

    @Test
    void testSelectFeatureWithNameAttribute() {
        // Arrange
        double x = 100.0;
        double y = 200.0;
        ExploreMapSelectInputData inputData = new ExploreMapSelectInputData(x, y);
        dataAccess.setFeatureToReturn(new TestSimpleFeature("Canada", "feature.1"));

        // Act
        interactor.selectFeature(inputData);

        // Assert
        assertTrue(dataAccess.getFeatureAtPositionWasCalled);
        assertNotNull(dataAccess.lastCoordinate);
        assertEquals(x, dataAccess.lastCoordinate.x, 0.001);
        assertEquals(y, dataAccess.lastCoordinate.y, 0.001);
        assertTrue(presenter.prepareFeatureSelectedViewWasCalled);
        assertNotNull(presenter.lastOutputData);
        assertNull(presenter.lastOutputData.getFeatureSource());
        assertNotNull(presenter.lastOutputData.getSelectedFeature());
        assertEquals("Canada", presenter.lastOutputData.getSelectedCountryName());
    }

    @Test
    void testSelectFeatureWithoutNameAttribute() {
        // Arrange
        double x = 150.0;
        double y = 250.0;
        ExploreMapSelectInputData inputData = new ExploreMapSelectInputData(x, y);
        dataAccess.setFeatureToReturn(new TestSimpleFeature(null, "feature.123"));

        // Act
        interactor.selectFeature(inputData);

        // Assert
        assertTrue(dataAccess.getFeatureAtPositionWasCalled);
        assertNotNull(dataAccess.lastCoordinate);
        assertEquals(x, dataAccess.lastCoordinate.x, 0.001);
        assertEquals(y, dataAccess.lastCoordinate.y, 0.001);
        assertTrue(presenter.prepareFeatureSelectedViewWasCalled);
        assertNotNull(presenter.lastOutputData);
        assertNull(presenter.lastOutputData.getFeatureSource());
        assertNotNull(presenter.lastOutputData.getSelectedFeature());
        assertEquals("feature.123", presenter.lastOutputData.getSelectedCountryName());
    }

    @Test
    void testSelectFeatureNoFeatureFound() {
        // Arrange
        double x = 300.0;
        double y = 400.0;
        ExploreMapSelectInputData inputData = new ExploreMapSelectInputData(x, y);
        dataAccess.setFeatureToReturn(null);

        // Act
        interactor.selectFeature(inputData);

        // Assert
        assertTrue(dataAccess.getFeatureAtPositionWasCalled);
        assertNotNull(dataAccess.lastCoordinate);
        assertEquals(x, dataAccess.lastCoordinate.x, 0.001);
        assertEquals(y, dataAccess.lastCoordinate.y, 0.001);
        assertTrue(presenter.prepareFeatureSelectedViewWasCalled);
        assertNotNull(presenter.lastOutputData);
        assertNull(presenter.lastOutputData.getFeatureSource());
        assertNull(presenter.lastOutputData.getSelectedFeature());
        assertNull(presenter.lastOutputData.getSelectedCountryName());
    }

    @Test
    void testSelectFeatureWithNonStringNameAttribute() {
        // Arrange: Test the toString() conversion path when NAME is a non-String object
        double x = 175.0;
        double y = 275.0;
        ExploreMapSelectInputData inputData = new ExploreMapSelectInputData(x, y);
        // Pass an Integer to force toString() conversion
        dataAccess.setFeatureToReturn(new TestSimpleFeatureWithObjectName(12345, "feature.999"));

        // Act
        interactor.selectFeature(inputData);

        // Assert
        assertTrue(dataAccess.getFeatureAtPositionWasCalled);
        assertTrue(presenter.prepareFeatureSelectedViewWasCalled);
        assertNotNull(presenter.lastOutputData);
        assertEquals("12345", presenter.lastOutputData.getSelectedCountryName());
    }

    // Test Double for ExploreMapDataAccessInterface
    private static class TestDataAccess implements ExploreMapDataAccessInterface {
        boolean loadShapefileWasCalled = false;
        boolean setFeatureSourceWasCalled = false;
        boolean getFeatureAtPositionWasCalled = false;
        String lastShapefilePath = null;
        Coordinate lastCoordinate = null;
        boolean shouldThrowException = false;
        SimpleFeature featureToReturn = null;

        void setShouldThrowException(boolean shouldThrow) {
            this.shouldThrowException = shouldThrow;
        }

        void setFeatureToReturn(SimpleFeature feature) {
            this.featureToReturn = feature;
        }

        @Override
        public SimpleFeatureSource loadShapefile(String filePath) throws IOException {
            loadShapefileWasCalled = true;
            lastShapefilePath = filePath;
            if (shouldThrowException) {
                throw new IOException("Test exception");
            }
            return new TestSimpleFeatureSource();
        }

        @Override
        public SimpleFeature getFeatureAtPosition(Coordinate coordinate) {
            getFeatureAtPositionWasCalled = true;
            lastCoordinate = coordinate;
            return featureToReturn;
        }

        @Override
        public ReferencedEnvelope getMaxBounds() {
            return null;
        }

        @Override
        public void setFeatureSource(SimpleFeatureSource featureSource) {
            setFeatureSourceWasCalled = true;
        }
    }

    // Test Double for ExploreMapOutputBoundary
    private static class TestPresenter implements ExploreMapOutputBoundary {
        boolean prepareMapLoadedViewWasCalled = false;
        boolean prepareFeatureSelectedViewWasCalled = false;
        boolean prepareFailViewWasCalled = false;
        ExploreMapOutputData lastOutputData = null;
        String lastErrorMessage = null;

        @Override
        public void prepareMapLoadedView(ExploreMapOutputData outputData) {
            prepareMapLoadedViewWasCalled = true;
            lastOutputData = outputData;
        }

        @Override
        public void prepareFeatureSelectedView(ExploreMapOutputData outputData) {
            prepareFeatureSelectedViewWasCalled = true;
            lastOutputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            prepareFailViewWasCalled = true;
            lastErrorMessage = errorMessage;
        }
    }

    // Minimal test implementation of SimpleFeatureSource
    private static class TestSimpleFeatureSource implements SimpleFeatureSource {
        @Override
        public Name getName() {
            return null;
        }

        @Override
        public org.geotools.api.data.DataAccess<SimpleFeatureType, SimpleFeature> getDataStore() {
            return null;
        }

        @Override
        public org.geotools.api.data.ResourceInfo getInfo() {
            return null;
        }

        @Override
        public org.geotools.api.data.QueryCapabilities getQueryCapabilities() {
            return null;
        }

        @Override
        public void addFeatureListener(org.geotools.api.data.FeatureListener listener) {
        }

        @Override
        public void removeFeatureListener(org.geotools.api.data.FeatureListener listener) {
        }

        @Override
        public SimpleFeatureType getSchema() {
            return null;
        }

        @Override
        public ReferencedEnvelope getBounds() {
            return null;
        }

        @Override
        public ReferencedEnvelope getBounds(org.geotools.api.data.Query query) {
            return null;
        }

        @Override
        public int getCount(org.geotools.api.data.Query query) {
            return 0;
        }

        @Override
        public org.geotools.data.simple.SimpleFeatureCollection getFeatures() {
            return null;
        }

        @Override
        public org.geotools.data.simple.SimpleFeatureCollection getFeatures(org.geotools.api.filter.Filter filter) {
            return null;
        }

        @Override
        public org.geotools.data.simple.SimpleFeatureCollection getFeatures(org.geotools.api.data.Query query) {
            return null;
        }

        @Override
        public java.util.Set<java.awt.RenderingHints.Key> getSupportedHints() {
            return null;
        }
    }

    // Minimal test implementation of SimpleFeature
    private static class TestSimpleFeature implements SimpleFeature {
        private final String name;
        private final String id;

        TestSimpleFeature(String name, String id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public FeatureId getIdentifier() {
            return null;
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public Object getAttribute(String name) {
            if ("NAME".equals(name)) {
                return this.name;
            }
            return null;
        }

        @Override
        public Object getAttribute(Name name) {
            return null;
        }

        @Override
        public Object getAttribute(int index) {
            return null;
        }

        @Override
        public int getAttributeCount() {
            return 0;
        }

        @Override
        public List<Object> getAttributes() {
            return null;
        }

        @Override
        public Object getDefaultGeometry() {
            return null;
        }

        @Override
        public SimpleFeatureType getFeatureType() {
            return null;
        }

        @Override
        public SimpleFeatureType getType() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object value) {
        }

        @Override
        public void setAttribute(Name name, Object value) {
        }

        @Override
        public void setAttribute(int index, Object value) {
        }

        @Override
        public void setAttributes(List<Object> values) {
        }

        @Override
        public void setAttributes(Object[] values) {
        }

        @Override
        public void setDefaultGeometry(Object geometry) {
        }

        @Override
        public org.geotools.api.geometry.BoundingBox getBounds() {
            return null;
        }

        @Override
        public void setDefaultGeometryProperty(org.geotools.api.feature.GeometryAttribute geometryAttribute) {
        }

        @Override
        public Collection<org.geotools.api.feature.Property> getProperties() {
            return null;
        }

        @Override
        public Collection<org.geotools.api.feature.Property> getProperties(Name name) {
            return null;
        }

        @Override
        public Collection<org.geotools.api.feature.Property> getProperties(String name) {
            return null;
        }

        @Override
        public org.geotools.api.feature.Property getProperty(Name name) {
            return null;
        }

        @Override
        public org.geotools.api.feature.Property getProperty(String name) {
            return null;
        }

        @Override
        public Collection<? extends org.geotools.api.feature.Property> getValue() {
            return null;
        }

        @Override
        public void setValue(Collection<org.geotools.api.feature.Property> values) {
        }

        @Override
        public void setValue(Object newValue) {
        }

        @Override
        public org.geotools.api.feature.GeometryAttribute getDefaultGeometryProperty() {
            return null;
        }

        @Override
        public Name getName() {
            return null;
        }

        @Override
        public boolean isNillable() {
            return false;
        }

        @Override
        public java.util.Map<Object, Object> getUserData() {
            return null;
        }

        @Override
        public org.geotools.api.feature.type.AttributeDescriptor getDescriptor() {
            return null;
        }

        @Override
        public void validate() {
        }
    }

    // Minimal test implementation of SimpleFeature with Object NAME attribute
    private static class TestSimpleFeatureWithObjectName implements SimpleFeature {
        private final Object nameObject;
        private final String id;

        TestSimpleFeatureWithObjectName(Object nameObject, String id) {
            this.nameObject = nameObject;
            this.id = id;
        }

        @Override
        public FeatureId getIdentifier() {
            return null;
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public Object getAttribute(String name) {
            if ("NAME".equals(name)) {
                return this.nameObject;  // Return the object directly, not converted to String
            }
            return null;
        }

        @Override
        public Object getAttribute(Name name) {
            return null;
        }

        @Override
        public Object getAttribute(int index) {
            return null;
        }

        @Override
        public int getAttributeCount() {
            return 0;
        }

        @Override
        public List<Object> getAttributes() {
            return null;
        }

        @Override
        public Object getDefaultGeometry() {
            return null;
        }

        @Override
        public SimpleFeatureType getFeatureType() {
            return null;
        }

        @Override
        public SimpleFeatureType getType() {
            return null;
        }

        @Override
        public void setAttribute(String name, Object value) {
        }

        @Override
        public void setAttribute(Name name, Object value) {
        }

        @Override
        public void setAttribute(int index, Object value) {
        }

        @Override
        public void setAttributes(List<Object> values) {
        }

        @Override
        public void setAttributes(Object[] values) {
        }

        @Override
        public void setDefaultGeometry(Object geometry) {
        }

        @Override
        public org.geotools.api.geometry.BoundingBox getBounds() {
            return null;
        }

        @Override
        public void setDefaultGeometryProperty(org.geotools.api.feature.GeometryAttribute geometryAttribute) {
        }

        @Override
        public Collection<org.geotools.api.feature.Property> getProperties() {
            return null;
        }

        @Override
        public Collection<org.geotools.api.feature.Property> getProperties(Name name) {
            return null;
        }

        @Override
        public Collection<org.geotools.api.feature.Property> getProperties(String name) {
            return null;
        }

        @Override
        public org.geotools.api.feature.Property getProperty(Name name) {
            return null;
        }

        @Override
        public org.geotools.api.feature.Property getProperty(String name) {
            return null;
        }

        @Override
        public Collection<? extends org.geotools.api.feature.Property> getValue() {
            return null;
        }

        @Override
        public void setValue(Collection<org.geotools.api.feature.Property> values) {
        }

        @Override
        public void setValue(Object newValue) {
        }

        @Override
        public org.geotools.api.feature.GeometryAttribute getDefaultGeometryProperty() {
            return null;
        }

        @Override
        public Name getName() {
            return null;
        }

        @Override
        public boolean isNillable() {
            return false;
        }

        @Override
        public java.util.Map<Object, Object> getUserData() {
            return null;
        }

        @Override
        public org.geotools.api.feature.type.AttributeDescriptor getDescriptor() {
            return null;
        }

        @Override
        public void validate() {
        }
    }
}
