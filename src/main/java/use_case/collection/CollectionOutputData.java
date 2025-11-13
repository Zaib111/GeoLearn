package use_case.collection;

import entity.Country;

import java.util.*;

public class CollectionOutputData {
    private final String collectionName;
    private final List<Country> countriesInCollection;

    public CollectionOutputData(String collectionName, List<Country> countriesInCollection) {
        this.collectionName = collectionName;
        this.countriesInCollection = countriesInCollection;
    }

    public String getCollectionName() { return collectionName; }

    public List<Country> getCountriesInCollection() { return countriesInCollection; }

}