package app.use_cases.country_collection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import app.entities.CountryCollection;

/**
 * Interface for data access operations related to CountryCollection entities.
 */
public interface CollectionDataAccessInterface {
    /**
     * Creates a new CountryCollection in the data store.
     *
     * @param countryCollection the CountryCollection to create
     */
    void createCollection(CountryCollection countryCollection);

    /**
     * Retrieves all CountryCollections from the data store.
     *
     * @return a list of all CountryCollections
     */
    List<CountryCollection> getAllCollections();

    /**
     * Retrieves a CountryCollection by its unique identifier.
     *
     * @param collectionId the UUID of the collection to retrieve
     * @return an Optional containing the CountryCollection if found, or empty if not found
     */
    Optional<CountryCollection> getCollectionById(UUID collectionId);

    /**
     * Deletes a CountryCollection by its unique identifier.
     *
     * @param collectionId the UUID of the collection to delete
     */
    void deleteCollection(UUID collectionId);

    /**
     * Updates an existing CountryCollection in the data store.
     *
     * @param updatedCollection the CountryCollection with updated information
     */
    void updateCollection(CountryCollection updatedCollection);
}
