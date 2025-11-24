package app.use_cases.explore_map;

/**
 * Input data for a hover interaction on the map.
 * Represents the on-screen position of the cursor when the user hovers.
 */
public class ExploreMapHoverInputData {

    /** The x-coordinate of the cursor on the screen. */
    private final double screenX;

    /** The y-coordinate of the cursor on the screen. */
    private final double screenY;

    /**
     * Creates a new input data object for a hover event.
     *
     * @param screenX the x-position of the cursor on the screen
     * @param screenY the y-position of the cursor on the screen
     */
    public ExploreMapHoverInputData(double screenX, double screenY) {
        this.screenX = screenX;
        this.screenY = screenY;
    }

    /**
     * Gets the screen x-coordinate of the cursor.
     *
     * @return the x position in pixels
     */
    public double getScreenX() {
        return screenX;
    }

    /**
     * Gets the screen y-coordinate of the cursor.
     *
     * @return the y position in pixels
     */
    public double getScreenY() {
        return screenY;
    }
}
