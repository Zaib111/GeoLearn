package app.use_cases.explore_map;

/**
 * Input data for selecting a feature on the map.
 */
public class ExploreMapSelectInputData {
    private final double x;
    private final double y;

    public ExploreMapSelectInputData(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
