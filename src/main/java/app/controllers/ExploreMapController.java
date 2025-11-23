package app.controllers;

import app.use_cases.explore_map.ExploreMapInputBoundary;
import app.use_cases.explore_map.ExploreMapInputData;
import app.use_cases.explore_map.ExploreMapSelectInputData;

/**
 * Controller for the Explore Map use case.
 * Simplified to only handle data operations
 * (loading map and selecting features).
 * Zoom/pan/mode changes are handled directly in the view.
 */
public class ExploreMapController {
    private final ExploreMapInputBoundary interactor;

    /**
     * Constructor for ExploreMapController.
     *
     * @param interactor the use case interactor
     */
    public ExploreMapController(final ExploreMapInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Load a map from a shapefile.
     *
     * @param shapefilePath the path to the shapefile
     */
    public void loadMap(final String shapefilePath) {
        final ExploreMapInputData inputData =
                new ExploreMapInputData(shapefilePath);
        interactor.loadMap(inputData);
    }

    /**
     * Select a feature at the given screen coordinates.
     *
     * @param screenX the x coordinate on the screen
     * @param screenY the y coordinate on the screen
     */
    public void selectFeature(final double screenX, final double screenY) {
        final ExploreMapSelectInputData inputData =
                new ExploreMapSelectInputData(screenX, screenY);
        interactor.selectFeature(inputData);
    }
}
