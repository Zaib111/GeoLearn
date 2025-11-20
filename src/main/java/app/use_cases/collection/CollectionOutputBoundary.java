package app.use_cases.collection;

import app.entities.CountryCollection;

import java.util.List;

public interface CollectionOutputBoundary {
    void prepareCollectionsView(List<CountryCollection> collections);
}
