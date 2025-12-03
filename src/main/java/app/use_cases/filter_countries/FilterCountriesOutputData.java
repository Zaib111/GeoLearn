package app.use_cases.filter_countries;

import java.util.List;

import app.entities.Country;

public class FilterCountriesOutputData {
    private final List<Country> countries;

    public FilterCountriesOutputData(List<Country> countries) {
        this.countries = countries;
    }

    public List<Country> getCountries() {
        return countries;
    }
}
