package use_case.compare;

import entity.Country;

/**
 * Data access boundary for the Compare use case.
 * The interactor depends on this interface, not on a concrete DAO.
 */
public interface CompareDataAccessInterface {

    /**
     * Returns the Country corresponding to the given country code,
     * or null if no such country exists.
     */
    Country getCountry(String countryCode);
}
