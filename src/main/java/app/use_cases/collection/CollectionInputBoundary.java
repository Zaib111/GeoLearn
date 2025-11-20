package app.use_cases.collection;

public interface CollectionInputBoundary {
    void addCollection(AddCollectionRequestData addCollectionRequestData);
    void fetchAllCollections();
}
