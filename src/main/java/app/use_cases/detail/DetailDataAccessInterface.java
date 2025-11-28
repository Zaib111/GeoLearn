package app.use_cases.detail;

import app.entities.Country;

import java.util.List;

/**
 * Data access interface for detail use case.
 */
public interface DetailDataAccessInterface {

    /**
     * Get a country by its code.
     * @param code the country code
     * @return the country
     */
    Country getCountryByCode(String code);

    /**
     * Get all countries.
     * @return list of all countries
     */
    List<Country> getCountries();

}
