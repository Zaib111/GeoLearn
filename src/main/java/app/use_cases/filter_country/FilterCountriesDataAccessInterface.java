package app.use_cases.filter_country;

import app.entities.Country;

import java.util.List;

public interface FilterCountriesDataAccessInterface {
    List<Country> getCountries();
}