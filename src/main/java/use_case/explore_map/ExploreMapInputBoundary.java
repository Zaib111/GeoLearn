package use_case.explore_map;

/**
 * The Explore Map Use Case Input Boundary.
 */
public interface ExploreMapInputBoundary {
    /**
     * Execute the load map use case.
     * @param inputData the input data containing the shapefile path
     */
    void loadMap(ExploreMapInputData inputData);

    /**
     * Execute the zoom in use case.
     * @param inputData the input data containing zoom parameters
     */
    void zoomIn(ExploreMapZoomInputData inputData);

    /**
     * Execute the zoom out use case.
     */
    void zoomOut();

    /**
     * Execute the reset view use case.
     */
    void resetView();

    /**
     * Execute the select feature use case.
     * @param inputData the input data containing the selected coordinates
     */
    void selectFeature(ExploreMapSelectInputData inputData);

    /**
     * Execute the hover feature use case.
     * @param inputData the input data containing the hover coordinates
     */
    void hoverFeature(ExploreMapHoverInputData inputData);

    /**
     * Execute the change interaction mode use case.
     * @param inputData the input data containing the new mode
     */
    void changeMode(ExploreMapModeInputData inputData);
}

