package use_case.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.locationtech.jts.geom.Coordinate;

/**
 * Interactor for the Explore Map use case.
 */
public class ExploreMapInteractor implements ExploreMapInputBoundary {
    private static final int MAX_ZOOM_IN_LEVELS = 5;

    private final ExploreMapDataAccessInterface dataAccess;
    private final ExploreMapOutputBoundary presenter;

    private SimpleFeature currentSelectedFeature;
    private SimpleFeature currentHoveredFeature;
    private int currentZoomLevel = 0;
    private String currentMode = "PAN";
    private ReferencedEnvelope currentDisplayArea;

    public ExploreMapInteractor(ExploreMapDataAccessInterface dataAccess,
                                ExploreMapOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void loadMap(ExploreMapInputData inputData) {
        try {
            SimpleFeatureSource featureSource = dataAccess.loadShapefile(inputData.getShapefilePath());
            dataAccess.setFeatureSource(featureSource);

            ReferencedEnvelope maxBounds = dataAccess.getMaxBounds();
            currentDisplayArea = maxBounds;
            currentZoomLevel = 0;

            ExploreMapOutputData outputData = new ExploreMapOutputData(
                null, null, maxBounds, 0, currentMode, null
            );
            presenter.prepareMapLoadedView(outputData);
        } catch (Exception e) {
            presenter.prepareFailView("Error loading map: " + e.getMessage());
        }
    }

    @Override
    public void zoomIn(ExploreMapZoomInputData inputData) {
        if (currentZoomLevel >= MAX_ZOOM_IN_LEVELS) {
            return;
        }

        currentZoomLevel++;

        ExploreMapOutputData outputData = new ExploreMapOutputData(
            currentSelectedFeature, currentHoveredFeature,
            currentDisplayArea, currentZoomLevel, currentMode,
            getSelectedCountryName()
        );
        presenter.prepareZoomView(outputData);
    }

    @Override
    public void zoomOut() {
        if (currentZoomLevel <= 0) {
            return;
        }

        currentZoomLevel--;

        ExploreMapOutputData outputData = new ExploreMapOutputData(
            currentSelectedFeature, currentHoveredFeature,
            currentDisplayArea, currentZoomLevel, currentMode,
            getSelectedCountryName()
        );
        presenter.prepareZoomView(outputData);
    }

    @Override
    public void resetView() {
        currentZoomLevel = 0;
        currentDisplayArea = dataAccess.getMaxBounds();

        ExploreMapOutputData outputData = new ExploreMapOutputData(
            currentSelectedFeature, currentHoveredFeature,
            currentDisplayArea, currentZoomLevel, currentMode,
            getSelectedCountryName()
        );
        presenter.prepareZoomView(outputData);
    }

    @Override
    public void selectFeature(ExploreMapSelectInputData inputData) {
        Coordinate coord = new Coordinate(inputData.getX(), inputData.getY());
        currentSelectedFeature = dataAccess.getFeatureAtPosition(coord);

        ExploreMapOutputData outputData = new ExploreMapOutputData(
            currentSelectedFeature, currentHoveredFeature,
            currentDisplayArea, currentZoomLevel, currentMode,
            getSelectedCountryName()
        );
        presenter.prepareFeatureSelectedView(outputData);
    }

    @Override
    public void hoverFeature(ExploreMapHoverInputData inputData) {
        Coordinate coord = new Coordinate(inputData.getX(), inputData.getY());
        SimpleFeature feature = dataAccess.getFeatureAtPosition(coord);

        if (feature != currentHoveredFeature) {
            currentHoveredFeature = feature;

            ExploreMapOutputData outputData = new ExploreMapOutputData(
                currentSelectedFeature, currentHoveredFeature,
                currentDisplayArea, currentZoomLevel, currentMode,
                getSelectedCountryName()
            );
            presenter.prepareFeatureHoverView(outputData);
        }
    }

    @Override
    public void changeMode(ExploreMapModeInputData inputData) {
        currentMode = inputData.getMode().name();

        if (!currentMode.equals("PAN") && !currentMode.equals("SELECT")) {
            currentHoveredFeature = null;
        }

        if (!currentMode.equals("SELECT")) {
            currentSelectedFeature = null;
        }

        ExploreMapOutputData outputData = new ExploreMapOutputData(
            currentSelectedFeature, currentHoveredFeature,
            currentDisplayArea, currentZoomLevel, currentMode,
            getSelectedCountryName()
        );
        presenter.prepareModeChangedView(outputData);
    }

    private String getSelectedCountryName() {
        if (currentSelectedFeature == null) {
            return null;
        }
        try {
            Object nameAttr = currentSelectedFeature.getAttribute("NAME");
            return nameAttr != null ? nameAttr.toString() : currentSelectedFeature.getID();
        } catch (Exception e) {
            return currentSelectedFeature.getID();
        }
    }
}
