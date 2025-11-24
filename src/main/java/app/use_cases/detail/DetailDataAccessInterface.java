package app.use_cases.detail;

import app.entities.Country;

import java.util.List;

public interface DetailDataAccessInterface {

    Country getCountryByName(String name);
    List<Country> getCountries();

}
