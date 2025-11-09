package entity;

import java.util.*;

public class Collection {
    private String collectionId;
    private String collectionName;
    private List<String> countryList;

    /**
     * Creates a new collection with the given non-empty collection ID and collection name, and a country list.
     * @param collectionId the unique identifier for the collection
     * @param collectionName the collection name
     * @param countryList the list of country codes for each country in the collection
     * @throws IllegalArgumentException if the collection ID, collection name or country list are empty
     */
    public Collection(String collectionId, String collectionName, List<String> countryList) {
        if ("".equals(collectionId)) {
            throw new IllegalArgumentException("collectionId can't be empty");
        }
        else if ("".equals(collectionName)) {
            throw new IllegalArgumentException("collectionName can't be empty");
        }
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.countryList = countryList;
    }

    public String getCollectionId() { return collectionId; }
    public String getCollectionName() { return collectionName; }
    public List<String> getCountryList() { return countryList; }

}
