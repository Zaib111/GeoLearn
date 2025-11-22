package adapters.ExploreMap;

import adapters.ViewModel;

/**
 * View Model for the Explore Map view.
 */
public class ExploreMapViewModel extends ViewModel {
    public static final String TITLE_LABEL = "GeoLearn - World Map Explorer";
    public static final String LOAD_MAP_BUTTON_LABEL = "Load World Map";
    public static final String ZOOM_IN_BUTTON_LABEL = "Zoom In";
    public static final String ZOOM_OUT_BUTTON_LABEL = "Zoom Out";
    public static final String RESET_BUTTON_LABEL = "Reset View";
    public static final String PAN_MODE_BUTTON_LABEL = "Pan Mode";
    public static final String ZOOM_MODE_BUTTON_LABEL = "Zoom Mode";
    public static final String SELECT_MODE_BUTTON_LABEL = "Select Mode";

    private ExploreMapState state = new ExploreMapState();

    public ExploreMapViewModel() {
        super("explore_map");
    }

    public ExploreMapState getState() {
        return state;
    }

    public void setState(ExploreMapState state) {
        this.state = state;
    }
}

