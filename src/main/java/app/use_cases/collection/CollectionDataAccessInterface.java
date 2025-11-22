package app.use_cases.collection;

import app.entities.CountryCollection;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CollectionDataAccessInterface {
    void createCollection(CountryCollection countryCollection);
    List<CountryCollection> getAllCollections();
    Optional<CountryCollection> getCollectionById(UUID collectionId);
    void deleteCollection(UUID collectionId);
    void updateCollection(CountryCollection updatedCollection);
}
