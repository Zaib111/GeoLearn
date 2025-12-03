package app.use_cases.filter_countries;

/**
 * Output boundary for the Filter Countries use case, which is responsible for presenting
 * the filtered list of countries back to the user interface layer.
 */
public interface FilterCountriesOutputBoundary {
    /**
     * Presents the filtered countries produced by the interactor.
     *
     * @param outputData the data containing the filtered list of countries
     */
    void presentFilteredCountries(FilterCountriesOutputData outputData);
}
