package app.use_cases.explore_map;

/**
 * Input data for a zoom action on the map.
 */
public class ExploreMapZoomInputData {

    /** X-coordinate of the zoom center on the screen. */
    private final double centerX;

    /** Y-coordinate of the zoom center on the screen. */
    private final double centerY;

    /** Whether the action is zooming in (true) or zooming out (false). */
    private final boolean isZoomIn;

    /**
     * Creates a zoom input data object.
     *
     * @param centerX the screen x-coordinate of the zoom center
     * @param centerY the screen y-coordinate of the zoom center
     * @param isZoomIn true if zooming in, false if zooming out
     */
    public ExploreMapZoomInputData(double centerX, double centerY, boolean isZoomIn) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.isZoomIn = isZoomIn;
    }

    /**
     * @return the screen x-coordinate of the zoom center
     */
    public double getCenterX() {
        return centerX;
    }

    /**
     * @return the screen y-coordinate of the zoom center
     */
    public double getCenterY() {
        return centerY;
    }

    /**
     * @return true if zooming in, false if zooming out
     */
    public boolean isZoomIn() {
        return isZoomIn;
    }
}
