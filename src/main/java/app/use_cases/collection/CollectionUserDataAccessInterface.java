package app.use_cases.collection;

import java.util.List;

import app.entities.CountryCollection;

public interface CollectionUserDataAccessInterface {
    /**
     * Creates a new collection.
     * @param countryCollection the collection instance to be created.
     */
    void createCollection(CountryCollection countryCollection);

    /**
     * Retrieves all collections.
     * @return a list of all country collections.
     */
    List<CountryCollection> getAllCollections();
}
