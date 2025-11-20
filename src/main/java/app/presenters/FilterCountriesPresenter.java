package app.presenters;

import app.entities.Country;
import app.use_cases.filter_country.FilterCountriesOutputBoundary;
import app.use_cases.filter_country.FilterCountriesOutputData;
import app.views.country_table.CountryTableView;

import java.util.List;

public class FilterCountriesPresenter implements FilterCountriesOutputBoundary {
    private final CountryTableView view;

    public FilterCountriesPresenter(CountryTableView view) {
        this.view = view;
    }

    @Override
    public void presentFilteredCountries(FilterCountriesOutputData outputData) {
        List<Country> filteredCountries = outputData.getCountries();

        view.displayCountries(filteredCountries);
    }
}
