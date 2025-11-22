package adapters.ExploreMap;

import use_case.explore_map.*;

/**
 * Controller for the Explore Map use case.
 */
public class ExploreMapController {
    private final ExploreMapInputBoundary interactor;

    public ExploreMapController(ExploreMapInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Load a map from a shapefile.
     * @param shapefilePath the path to the shapefile
     */
    public void loadMap(String shapefilePath) {
        ExploreMapInputData inputData = new ExploreMapInputData(shapefilePath);
        interactor.loadMap(inputData);
    }

    /**
     * Zoom in on the map.
     * @param centerX the x coordinate to zoom at
     * @param centerY the y coordinate to zoom at
     */
    public void zoomIn(double centerX, double centerY) {
        ExploreMapZoomInputData inputData = new ExploreMapZoomInputData(centerX, centerY, true);
        interactor.zoomIn(inputData);
    }

    /**
     * Zoom out on the map.
     */
    public void zoomOut() {
        interactor.zoomOut();
    }

    /**
     * Reset the map view to default.
     */
    public void resetView() {
        interactor.resetView();
    }

    /**
     * Select a feature at the given coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void selectFeature(double x, double y) {
        ExploreMapSelectInputData inputData = new ExploreMapSelectInputData(x, y);
        interactor.selectFeature(inputData);
    }

    /**
     * Hover over a feature at the given coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void hoverFeature(double x, double y) {
        ExploreMapHoverInputData inputData = new ExploreMapHoverInputData(x, y);
        interactor.hoverFeature(inputData);
    }

    /**
     * Change the interaction mode.
     * @param mode the new mode (PAN, ZOOM, or SELECT)
     */
    public void changeMode(String mode) {
        ExploreMapModeInputData.InteractionMode interactionMode = 
            ExploreMapModeInputData.InteractionMode.valueOf(mode);
        ExploreMapModeInputData inputData = new ExploreMapModeInputData(interactionMode);
        interactor.changeMode(inputData);
    }
}

