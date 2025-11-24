package app.use_cases.explore_map;

/**
 * Input data for selecting a feature on the map.
 */
public class ExploreMapSelectInputData {

    /** Screen x-coordinate of the selection point. */
    private final double x;

    /** Screen y-coordinate of the selection point. */
    private final double y;

    /**
     * Creates input data for a selection event.
     *
     * @param screenX the x-position of the selection on the screen
     * @param screenY the y-position of the selection on the screen
     */
    public ExploreMapSelectInputData(double screenX, double screenY) {
        this.x = screenX;
        this.y = screenY;
    }

    /**
     * Returns the screen x-coordinate of the selected position.
     *
     * @return the x position in screen pixels
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the screen y-coordinate of the selected position.
     *
     * @return the y position in screen pixels
     */
    public double getY() {
        return y;
    }
}
