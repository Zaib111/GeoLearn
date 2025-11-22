package adapters.ExploreMap;
import adapters.ViewModel;
import use_case.explore_map.ExploreMapOutputBoundary;
import use_case.explore_map.ExploreMapOutputData;
/**
 * Presenter for the Explore Map use case.
 * Simplified to only handle data operations.
 */
public class ExploreMapPresenter implements ExploreMapOutputBoundary {
    private final ViewModel<ExploreMapState> viewModel;
    public ExploreMapPresenter(ViewModel<ExploreMapState> viewModel) {
        this.viewModel = viewModel;
    }
    @Override
    public void prepareMapLoadedView(ExploreMapOutputData outputData) {
        ExploreMapState state = viewModel.getState();
        state.setFeatureSource(outputData.getFeatureSource());
        state.setMapLoaded(true);
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
    public void prepareFailView(String errorMessage) {
        ExploreMapState state = viewModel.getState();
        state.setErrorMessage(errorMessage);
        viewModel.setState(state);
        viewModel.firePropertyChange();
    }
}