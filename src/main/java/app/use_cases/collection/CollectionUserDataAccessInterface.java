package app.use_cases.collection;

import app.entities.CountryCollection;

import java.util.List;

public interface CollectionUserDataAccessInterface {
    void createCollection(CountryCollection countryCollection);
    List<CountryCollection> getAllCollections();
}
