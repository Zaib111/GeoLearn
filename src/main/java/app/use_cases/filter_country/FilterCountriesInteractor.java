package app.use_cases.filter_country;

import app.entities.Country;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FilterCountriesInteractor implements FilterCountriesInputBoundary {
    private final FilterCountriesDataAccessInterface dataAccess;
    private final FilterCountriesOutputBoundary presenter;

    public FilterCountriesInteractor(FilterCountriesDataAccessInterface dataAccess, FilterCountriesOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void filterCountries(FilterCountriesInputData inputData) {
        List<Country> allCountries = dataAccess.getCountries();
        List<Country> filteredCountries = new ArrayList<>();

        String searchTerm = inputData.getSearchTerm();
        String region = inputData.getRegion();
        String subregion = inputData.getSubregion();

        for (Country country : allCountries) {
            if (matchesSearch(country, searchTerm) && matchesRegion(country, region) && matchesSubregion(country, subregion)) {
                filteredCountries.add(country);
            }
        }

        FilterCountriesOutputData outputData = new FilterCountriesOutputData(filteredCountries);

        presenter.presentFilteredCountries(outputData);
    }

    private static boolean matchesSubregion(Country country, String subregion) {
        return subregion.equals(country.getSubregion().orElse(null)) || subregion.equals("Any");
    }

    private static boolean matchesRegion(Country country, String region) {
        return region.equals(country.getRegion()) || region.equals("Any");
    }

    private boolean matchesSearch(Country country, String searchTerm) {
        return country.getName().toLowerCase().contains(searchTerm.toLowerCase());
    }
}