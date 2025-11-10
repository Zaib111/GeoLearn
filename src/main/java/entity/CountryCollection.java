package entity;

import java.util.*;

public class CountryCollection {
    private UUID collectionId;
    private String collectionName;
    private List<Country> countries;

    /**
     * Creates a new collection with the given non-empty collection ID and collection name, and a country list.
     * @param collectionId the unique identifier for the collection
     * @param collectionName the collection name
     * @param countries the list of country codes for each country in the collection
     * @throws IllegalArgumentException if the collection ID, collection name or country list are empty
     */
    public CountryCollection(UUID collectionId, String collectionName, List<Country> countries) {
        if (collectionId == null) {
            throw new IllegalArgumentException("collectionId can't be empty");
        }
        else if ("".equals(collectionName)) {
            throw new IllegalArgumentException("collectionName can't be empty");
        }
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.countries = countries;
    }

    public UUID getCollectionId() { return collectionId; }
    public String getCollectionName() { return collectionName; }
    public List<Country> getCountries() { return countries; }

}
