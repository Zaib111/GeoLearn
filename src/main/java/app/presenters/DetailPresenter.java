package app.presenters;

import app.use_cases.detail.DetailOutputBoundary;
import app.use_cases.detail.DetailOutputData;
import app.views.ViewModel;
import app.views.detail.DetailState;

/**
 * Presenter for the Detail Use Case.
 * Receives output data from the Interactor and formats it into the ViewModel state
 * so the View can display it.
 */
public class DetailPresenter implements DetailOutputBoundary {
    // The ViewModel that holds the state for the Detail View
    private final ViewModel<DetailState> detailViewModel;

    /**
     * Constructor for the Presenter.
     *
     * @param detailViewModel The ViewModel to be updated with country details.
     */
    public DetailPresenter(ViewModel<DetailState> detailViewModel) {
        this.detailViewModel = detailViewModel;
    }

    /**
     * Prepares the success view by populating the ViewModel state with the fetched country data.
     *
     * @param detailOutputData The raw output data received from the Interactor.
     */
    @Override
    public void prepareDetailSuccessView(DetailOutputData detailOutputData) {
        // Retrieve the current state object from the ViewModel
        DetailState state = detailViewModel.getState();

        // Update state with basic identifiers
        state.setCountryCode(detailOutputData.getCountryCode());
        state.setCountryName(detailOutputData.getCountryName());

        // Update state with geographic details
        state.setCapital(detailOutputData.getCapital());
        state.setRegion(detailOutputData.getRegion());
        state.setSubregion(detailOutputData.getSubregion());

        // Update state with statistical data
        state.setPopulation(detailOutputData.getPopulation());
        state.setAreaKm2(detailOutputData.getAreaKm2());

        // Update state with list-based data and flag URL
        state.setBorders(detailOutputData.getBorders());
        state.setFlagUrl(detailOutputData.getFlagUrl());
        state.setLanguages(detailOutputData.getLanguages());
        state.setCurrencies(detailOutputData.getCurrencies());
        state.setTimezones(detailOutputData.getTimezones());

        // Reset error flags as the operation was successful
        state.setErrorMessage("");
        state.setHasError(false);

        // Notify the ViewModel observers that the state has changed
        detailViewModel.updateState(state);
    }

    /**
     * Prepares the failure view by updating the ViewModel state with an error message.
     *
     * @param errorMessage The error message describing why the operation failed.
     */
    @Override
    public void prepareDetailFailureView(String errorMessage) {
        // Retrieve the current state object
        DetailState state = detailViewModel.getState();

        // Set the error message and toggle the error flag
        state.setErrorMessage(errorMessage);
        state.setHasError(true);

        // Notify the ViewModel observers to trigger the error view
        detailViewModel.updateState(state);
    }
}