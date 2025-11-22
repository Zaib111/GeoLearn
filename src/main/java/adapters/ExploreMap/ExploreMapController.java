package adapters.ExploreMap;

import use_case.explore_map.ExploreMapInputBoundary;
import use_case.explore_map.ExploreMapInputData;
import use_case.explore_map.ExploreMapSelectInputData;

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
     * @param interactor the use case interactor
     */
    public ExploreMapController(final ExploreMapInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Load a map from a shapefile.
     * @param shapefilePath the path to the shapefile
     */
    public void loadMap(final String shapefilePath) {
        final ExploreMapInputData inputData =
                new ExploreMapInputData(shapefilePath);
        interactor.loadMap(inputData);
    }

    /**
     * Select a feature at the given coordinates.
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void selectFeature(final double x, final double y) {
        final ExploreMapSelectInputData inputData =
                new ExploreMapSelectInputData(x, y);
        interactor.selectFeature(inputData);
    }
}
