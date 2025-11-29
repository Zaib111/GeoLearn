package app.use_cases.filter_country;

import java.util.ArrayList;
import java.util.List;

import app.data_access.APICountryDataAccessObject;
import app.entities.Country;

public class FilterCountriesInteractor implements FilterCountriesInputBoundary {
    private final APICountryDataAccessObject dataAccess;
    private final FilterCountriesOutputBoundary presenter;

    public FilterCountriesInteractor(APICountryDataAccessObject dataAccess, FilterCountriesOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void filterCountries(FilterCountriesInputData inputData) {
        final List<Country> allCountries = dataAccess.getCountries();
        final List<Country> filteredCountries = new ArrayList<>();

        final String searchTerm = inputData.getSearchTerm();
        final String region = inputData.getRegion();
        final String subregion = inputData.getSubregion();

        for (Country country : allCountries) {
            if (matchesSearch(country, searchTerm)
                    && matchesRegion(country, region)
                    && matchesSubregion(country, subregion)) {
                filteredCountries.add(country);
            }
        }

        final FilterCountriesOutputData outputData = new FilterCountriesOutputData(filteredCountries);

        presenter.presentFilteredCountries(outputData);
    }

    private static boolean matchesSubregion(Country country, String subregion) {
        return subregion.equals(country.getSubregion().orElse(null)) || "Any".equals(subregion);
    }

    private static boolean matchesRegion(Country country, String region) {
        return region.equals(country.getRegion()) || "Any".equals(region);
    }

    private boolean matchesSearch(Country country, String searchTerm) {
        return country.getName().toLowerCase().contains(searchTerm.toLowerCase());
    }
}
