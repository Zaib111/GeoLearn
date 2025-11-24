package app.presenters;

import app.use_cases.detail.DetailOutputBoundary;
import app.use_cases.detail.DetailOutputData;
import app.views.ViewModel;
import app.views.detail.DetailState;

public class DetailPresenter implements DetailOutputBoundary{
    private final ViewModel<DetailState> detailViewModel;
    public DetailPresenter(ViewModel<DetailState> detailViewModel) {
        this.detailViewModel = detailViewModel;
    }

    /*@Override
    public void prepareFailureView(String errorMessage) {

    }*/

    @Override
    public void prepareDetailSuccessView(DetailOutputData detailOutputData) {
        DetailState state = detailViewModel.getState();
        state.setCountryCode(detailOutputData.getCountryCode());
        state.setCountryName(detailOutputData.getCountryName());
        state.setCapital(detailOutputData.getCapital());
        state.setRegion(detailOutputData.getRegion());
        state.setSubregion(detailOutputData.getSubregion());
        state.setPopulation(detailOutputData.getPopulation());
        state.setAreaKm2(detailOutputData.getAreaKm2());
        state.setBorders(detailOutputData.getBorders());
        state.setFlagUrl(detailOutputData.getFlagUrl());
        state.setLanguages(detailOutputData.getLanguages());
        state.setCurrencies(detailOutputData.getCurrencies());
        state.setTimezones(detailOutputData.getTimezones());
        state.setErrorMessage("");
        detailViewModel.updateState(state);
    }

    @Override
    public void prepareDetailFailureView(String errorMessage) {
        DetailState state = detailViewModel.getState();
        state.setErrorMessage(errorMessage);
        detailViewModel.updateState(state);
    }
}
