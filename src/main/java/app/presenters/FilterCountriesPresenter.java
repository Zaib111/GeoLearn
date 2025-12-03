package app.presenters;

import java.util.List;

import app.entities.Country;
import app.use_cases.filter_countries.FilterCountriesOutputBoundary;
import app.use_cases.filter_countries.FilterCountriesOutputData;
import app.views.ViewModel;
import app.views.filter_countries.FilterCountriesState;

public class FilterCountriesPresenter implements FilterCountriesOutputBoundary {
    private final ViewModel<FilterCountriesState> filterCountriesViewModel;

    public FilterCountriesPresenter(ViewModel<FilterCountriesState> filterCountriesViewModel) {
        this.filterCountriesViewModel = filterCountriesViewModel;
    }

    @Override
    public void presentFilteredCountries(FilterCountriesOutputData outputData) {
        final FilterCountriesState filterCountriesState = new FilterCountriesState();
        // get filtered countries from output data
        final List<Country> filteredCountries = outputData.getCountries();
        filterCountriesState.setFilteredCountries(filteredCountries);
        filterCountriesViewModel.updateState(filterCountriesState);
    }
}
