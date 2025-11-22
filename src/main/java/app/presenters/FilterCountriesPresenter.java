package app.presenters;

import app.entities.Country;
import app.use_cases.filter_country.FilterCountriesOutputBoundary;
import app.use_cases.filter_country.FilterCountriesOutputData;
import app.views.ViewModel;
import app.views.filter_countries.FilterCountriesState;
import app.views.filter_countries.FilterCountriesView;
import app.views.settings.SettingsState;

import java.util.List;

public class FilterCountriesPresenter implements FilterCountriesOutputBoundary {
    private final ViewModel<FilterCountriesState> filterCountriesViewModel;

    public FilterCountriesPresenter(ViewModel<FilterCountriesState> filterCountriesViewModel) {
        this.filterCountriesViewModel = filterCountriesViewModel;
    }

    @Override
    public void presentFilteredCountries(FilterCountriesOutputData outputData) {
        FilterCountriesState filterCountriesState = new FilterCountriesState();
        List<Country> filteredCountries = outputData.getCountries(); // get filtered countries from output data
        filterCountriesState.setFilteredCountries(filteredCountries);
        filterCountriesViewModel.updateState(filterCountriesState);
    }
}
