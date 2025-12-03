package app.use_cases.filter_countries;

import java.util.List;

import app.entities.Country;

/**
 * Interface for data access operations related to FilterCountries entities.
 */
public interface FilterCountriesDataAccessInterface {
    /**
     * Retrieves all Countries from the data store.
     *
     * @return a list of all Countries
     */
    List<Country> getCountries();
}
