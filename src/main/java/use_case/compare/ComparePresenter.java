package use_case.compare;

import use_case.compare.CompareOutputBoundary;
import use_case.compare.CompareOutputData;

public class ComparePresenter implements CompareOutputBoundary {

    private final CompareViewModel viewModel;

    public ComparePresenter(CompareViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(CompareOutputData outputData) {
        viewModel.clearError();
        // For now, just store the names; later you can store full table data
        viewModel.setSelectedCountries(
                outputData.getCountries().stream()
                        .map(CompareOutputData.CountryRow::getName)
                        .collect(java.util.stream.Collectors.toList())
        );
    }

    @Override
    public void prepareFailView(String errorMessage) {
        viewModel.setError(errorMessage);
    }
}
