package app.use_cases.explore_map;

/**
 * Input data for zoom operations.
 */
public class ExploreMapZoomInputData {
    private final double centerX;
    private final double centerY;
    private final boolean isZoomIn;

    public ExploreMapZoomInputData(double centerX, double centerY, boolean isZoomIn) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.isZoomIn = isZoomIn;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public boolean isZoomIn() {
        return isZoomIn;
    }
}
