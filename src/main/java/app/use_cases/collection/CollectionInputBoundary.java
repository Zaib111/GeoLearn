package app.use_cases.collection;

public interface CollectionInputBoundary {
    void addCollection(AddCollectionRequestData addCollectionRequestData);
    void fetchAllCollections();
    void deleteCollection(DeleteCollectionRequestData deleteCollectionRequestData);
    void renameCollection(RenameCollectionRequestData renameCollectionRequestData);
    void editCollection(EditCollectionRequestData editCollectionRequestData);
}
