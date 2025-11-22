package app.use_cases.compare;

import app.entities.Country;

import java.util.List;

public interface CompareDataAccessInterface {

    List<String> getAllCountryNames();

    List<Country> getCountriesByNames(List<String> names);
}
