package app.use_cases.explore_map;

/**
 * Input data for changing the interaction mode.
 */
public class ExploreMapModeInputData {

    /**
     * Supported user interaction modes.
     */
    public enum InteractionMode {
        PAN,
        ZOOM,
        SELECT
    }

    /** The chosen interaction mode. */
    private final InteractionMode mode;

    /**
     * Create input data with a selected interaction mode.
     *
     * @param mode the new user interaction mode
     */
    public ExploreMapModeInputData(InteractionMode mode) {
        this.mode = mode;
    }

    /**
     * @return the selected interaction mode
     */
    public InteractionMode getMode() {
        return mode;
    }
}
