package adapters.ExploreMap;

import use_case.explore_map.*;

/**
 * Controller for the Explore Map use case.
 * Simplified to only handle data operations (loading map and selecting features).
 * Zoom/pan/mode changes are handled directly in the view.
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
     * Select a feature at the given coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void selectFeature(double x, double y) {
        ExploreMapSelectInputData inputData = new ExploreMapSelectInputData(x, y);
        interactor.selectFeature(inputData);
    }
}
