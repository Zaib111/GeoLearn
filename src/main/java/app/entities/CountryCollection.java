package app.entities;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CountryCollection {
    private final UUID collectionId;
    private final String collectionName;
    private final List<Country> countries;

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
}
