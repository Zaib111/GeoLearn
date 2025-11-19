package use_case.filter_country;

import entity.Country;

import java.util.List;

public class FilterCountriesOutputData {
    private final List<Country> countries;

    public FilterCountriesOutputData(List<Country> countries) {
        this.countries = countries;
    }

    public List<Country> getCountries() {
        return countries;
    }
}
