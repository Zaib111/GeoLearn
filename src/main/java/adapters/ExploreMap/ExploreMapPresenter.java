package adapters.ExploreMap;

import adapters.ViewManagerModel;
import use_case.explore_map.ExploreMapOutputBoundary;
import use_case.explore_map.ExploreMapOutputData;

/**
 * Presenter for the Explore Map use case.
 */
public class ExploreMapPresenter implements ExploreMapOutputBoundary {
    private final ExploreMapViewModel viewModel;
    private final ViewManagerModel viewManagerModel;

    public ExploreMapPresenter(ViewManagerModel viewManagerModel, ExploreMapViewModel viewModel) {
        this.viewManagerModel = viewManagerModel;
        this.viewModel = viewModel;
    }

    @Override
    public void prepareMapLoadedView(ExploreMapOutputData outputData) {
        ExploreMapState state = viewModel.getState();
        state.setDisplayArea(outputData.getDisplayArea());
        state.setZoomLevel(outputData.getZoomLevel());
        state.setInteractionMode(outputData.getInteractionMode());
        state.setMapLoaded(true);
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareZoomView(ExploreMapOutputData outputData) {
        ExploreMapState state = viewModel.getState();
        state.setDisplayArea(outputData.getDisplayArea());
        state.setZoomLevel(outputData.getZoomLevel());
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFeatureSelectedView(ExploreMapOutputData outputData) {
        ExploreMapState state = viewModel.getState();
        state.setSelectedFeature(outputData.getSelectedFeature());
        state.setSelectedCountryName(outputData.getSelectedCountryName());
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFeatureHoverView(ExploreMapOutputData outputData) {
        ExploreMapState state = viewModel.getState();
        state.setHoveredFeature(outputData.getHoveredFeature());
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareModeChangedView(ExploreMapOutputData outputData) {
        ExploreMapState state = viewModel.getState();
        state.setInteractionMode(outputData.getInteractionMode());
        state.setSelectedFeature(outputData.getSelectedFeature());
        state.setHoveredFeature(outputData.getHoveredFeature());
        state.setErrorMessage(null);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ExploreMapState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}
