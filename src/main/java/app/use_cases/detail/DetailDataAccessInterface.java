package app.use_cases.detail;

import app.entities.Country;

import java.util.List;

/**
 * Data Access Interface for the Detail Use Case.
 * Defines the methods required by the Interactor to retrieve country data from the persistence layer.
 */
public interface DetailDataAccessInterface {

    /**
     * Retrieves a Country entity by its unique code (e.g., "CCA3").
     *
     * @param code The country code string.
     * @return The Country entity if found, or null if not.
     */
    Country getCountryByCode(String code);

    /**
     * Retrieves a Country entity by its common name.
     *
     * @param name The country name string.
     * @return The Country entity if found, or null if not.
     */
    Country getCountryByName(String name);

    /**
     * Retrieves a list of all available countries.
     *
     * @return A List of all Country entities.
     */
    List<Country> getCountries();

}