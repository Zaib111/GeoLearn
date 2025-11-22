package app.use_cases.explore_map;

/**
 * The Explore Map Use Case Input Boundary.
 * Only handles data operations - UI operations like zoom/pan are handled in the view.
 */
public interface ExploreMapInputBoundary {
    /**
     * Execute the load map use case.
     * @param inputData the input data containing the shapefile path
     */
    void loadMap(ExploreMapInputData inputData);

    /**
     * Execute the select feature use case.
     * @param inputData the input data containing the selected coordinates
     */
    void selectFeature(ExploreMapSelectInputData inputData);
}
