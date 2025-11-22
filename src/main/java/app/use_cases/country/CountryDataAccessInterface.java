package app.use_cases.country;

import app.entities.Country;

import java.util.List;

public interface CountryDataAccessInterface {
    List<Country> getCountries();
    Country getCountry(String countryCode);
}
