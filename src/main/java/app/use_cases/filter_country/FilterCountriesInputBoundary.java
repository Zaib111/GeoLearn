package app.use_cases.filter_country;

public interface FilterCountriesInputBoundary {
    void filterCountries(FilterCountriesInputData inputData);
    void openCountryDetails(String countryName);
}
