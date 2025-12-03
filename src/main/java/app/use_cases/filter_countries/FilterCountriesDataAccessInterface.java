package app.use_cases.filter_countries;

import app.entities.Country;

import java.util.List;

public interface FilterCountriesDataAccessInterface {
    List<Country> getCountries();
}