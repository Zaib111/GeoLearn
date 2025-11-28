package app.use_cases.detail;

import app.entities.Country;

import java.util.List;

public interface DetailDataAccessInterface {

    Country getCountryByCode(String code);
    List<Country> getCountries();

}
