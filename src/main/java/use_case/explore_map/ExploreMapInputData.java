package use_case.explore_map;

/**
 * Input data for loading a map.
 */
public class ExploreMapInputData {
    private final String shapefilePath;

    public ExploreMapInputData(String shapefilePath) {
        this.shapefilePath = shapefilePath;
    }

    public String getShapefilePath() {
        return shapefilePath;
    }
}

