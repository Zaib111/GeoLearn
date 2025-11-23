package app.use_cases.explore_map;

/**
 * Input data for requesting a map to be loaded in the Explore Map use case.
 * Contains the file system path to the shapefile that should be loaded.
 */
public class ExploreMapInputData {

    /** The path to the shapefile containing geographical data. */
    private final String shapefilePath;

    /**
     * Creates a new input data object for a map loading request.
     *
     * @param shapefilePath the file path to the shapefile to load
     */
    public ExploreMapInputData(String shapefilePath) {
        this.shapefilePath = shapefilePath;
    }

    /**
     * Gets the shapefile path associated with the map load request.
     *
     * @return the shapefile file path as a string
     */
    public String getShapefilePath() {
        return shapefilePath;
    }
}
