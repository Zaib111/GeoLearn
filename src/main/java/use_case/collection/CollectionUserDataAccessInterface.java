package use_case.collection;

import entity.CountryCollection;

import java.util.*;

/**
 * The DAO interface for the Country Collection Use Case.
 */
public interface CollectionUserDataAccessInterface {

    /**
     * Updates the system to create a new collection.
     * @param countryCollection the collection which we want to create
     */
    void createCollection(CountryCollection countryCollection);

    /**
     * Retrieves all country collections from the system.
     * @return a list of all country collections
     */
    List<CountryCollection> getAllCollections();

    /**
     * Retrieves a country collection by its ID.
     * @param collectionId the unique identifier for the collection
     * @return the country collection with the given ID
     */
    CountryCollection getCollectionById(UUID collectionId);
}
