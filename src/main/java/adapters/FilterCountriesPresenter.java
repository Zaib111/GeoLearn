package adapters;

import entity.Country;
import use_case.filter_country.FilterCountriesOutputBoundary;
import use_case.filter_country.FilterCountriesOutputData;
import view.CountryTableView;

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
