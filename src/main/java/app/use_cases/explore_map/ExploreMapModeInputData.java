package app.use_cases.explore_map;

/**
 * Input data for changing the interaction mode.
 */
public class ExploreMapModeInputData {
    public enum InteractionMode {
        PAN, ZOOM, SELECT
    }

    private final InteractionMode mode;

    public ExploreMapModeInputData(InteractionMode mode) {
        this.mode = mode;
    }

    public InteractionMode getMode() {
        return mode;
    }
}
