package use_case.country;

import entity.Country;

import java.util.List;

public interface CountryDataAccessInterface {
    List<Country> getCountries();
    Country getCountry(String countryCode);
}
