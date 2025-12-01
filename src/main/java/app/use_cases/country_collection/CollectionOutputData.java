package app.use_cases.country_collection;

import java.util.Collections;
import java.util.List;

import app.entities.CountryCollection;

/**
 * Output data for the Collection use case.
 * Encapsulates the data that needs to be passed to the presenter.
 */
public class CollectionOutputData {
    private final List<CountryCollection> collections;

    /**
     * Constructs output data with a list of country collections.
     *
     * @param collections the list of country collections
     */
    public CollectionOutputData(List<CountryCollection> collections) {
        this.collections = Collections.unmodifiableList(collections);
    }

    /**
     * Gets the list of country collections.
     *
     * @return an unmodifiable list of country collections
     */
    public List<CountryCollection> getCollections() {
        return collections;
    }
}

