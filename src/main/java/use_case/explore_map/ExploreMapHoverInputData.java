package use_case.explore_map;

/**
 * Input data for hovering over a feature on the map.
 */
public class ExploreMapHoverInputData {
    private final double x;
    private final double y;

    public ExploreMapHoverInputData(double x, double y) {
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

