package app.presenters;

import app.use_cases.explore_map.ExploreMapOutputBoundary;
import app.use_cases.explore_map.ExploreMapOutputData;
import app.views.ViewModel;
import app.views.explore_map.ExploreMapState;

/**
 * Presenter for the Explore Map use case.
 * Translates use case output into updates on the {@link ExploreMapState}
 * stored in the view model.
 */
public class ExploreMapPresenter implements ExploreMapOutputBoundary {

    /** View model holding the current Explore Map state. */
    private final ViewModel<ExploreMapState> viewModel;

    /**
     * Creates a presenter for the Explore Map use case.
     *
     * @param viewModel the view model to update
     */
    public ExploreMapPresenter(final ViewModel<ExploreMapState> viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * Updates the view model when a map is successfully loaded.
     *
     * @param outputData output data containing the loaded feature source
     */
    @Override
    public void prepareMapLoadedView(final ExploreMapOutputData outputData) {
        final ExploreMapState state = viewModel.getState();
        state.setFeatureSource(outputData.getFeatureSource());
        state.setMapLoaded(true);
        state.setErrorMessage(null);
        viewModel.updateState(state);
    }

    /**
     * Updates the view model when a feature is selected.
     *
     * @param outputData output data containing the selected feature and name
     */
    @Override
    public void prepareFeatureSelectedView(final ExploreMapOutputData outputData) {
        final ExploreMapState state = viewModel.getState();
        state.setSelectedFeature(outputData.getSelectedFeature());
        state.setSelectedCountryName(outputData.getSelectedCountryName());
        state.setErrorMessage(null);
        viewModel.updateState(state);
    }

    /**
     * Updates the view model when an error occurs.
     *
     * @param errorMessage description of the error
     */
    @Override
    public void prepareFailView(final String errorMessage) {
        final ExploreMapState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.updateState(state);
    }
}
