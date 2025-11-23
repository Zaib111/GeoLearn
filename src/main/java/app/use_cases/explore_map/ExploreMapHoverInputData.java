package app.use_cases.explore_map;

/**
 * Input data for hovering over a feature on the map.
 */
public class ExploreMapHoverInputData {
    private final double screenX;
    private final double screenY;

    public ExploreMapHoverInputData(double screenX, double screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
    }

    public double getScreenX() {
        return screenX;
    }

    public double getScreenY() {
        return screenY;
    }
}
