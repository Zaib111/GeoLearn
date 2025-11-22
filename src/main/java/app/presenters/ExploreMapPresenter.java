package app.presenters;

import app.use_cases.explore_map.ExploreMapOutputBoundary;
import app.use_cases.explore_map.ExploreMapOutputData;
import app.views.ViewModel;
import app.views.explore_map.ExploreMapState;

/**
 * Presenter for the Explore Map use case.
 */
public class ExploreMapPresenter implements ExploreMapOutputBoundary {
    private final ViewModel<ExploreMapState> viewModel;

    /**
     * Constructor for ExploreMapPresenter.
     * @param viewModel the view model
     */
    public ExploreMapPresenter(final ViewModel<ExploreMapState> viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareMapLoadedView(final ExploreMapOutputData outputData) {
        final ExploreMapState state = viewModel.getState();
        state.setFeatureSource(outputData.getFeatureSource());
        state.setMapLoaded(true);
        state.setErrorMessage(null);
        viewModel.updateState(state);
    }

    @Override
    public void prepareFeatureSelectedView(final ExploreMapOutputData outputData) {
        final ExploreMapState state = viewModel.getState();
        state.setSelectedFeature(outputData.getSelectedFeature());
        state.setSelectedCountryName(outputData.getSelectedCountryName());
        state.setErrorMessage(null);
        viewModel.updateState(state);
    }

    @Override
    public void prepareFailView(final String errorMessage) {
        final ExploreMapState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.updateState(state);
    }
}

