package app.use_cases.country_collection;

/**
 * Input boundary interface for collection-related use cases.
 * Defines methods for adding, fetching, deleting, renaming, and editing collections.
 */
public interface CollectionInputBoundary {
    /**
     * Adds a new collection.
     *
     * @param addCollectionRequestData the request data for adding a collection
     */
    void addCollection(AddCollectionRequestData addCollectionRequestData);

    /**
     * Fetches all collections.
     */
    void fetchAllCollections();

    /**
     * Deletes a collection.
     *
     * @param deleteCollectionRequestData the request data for deleting a collection
     */
    void deleteCollection(DeleteCollectionRequestData deleteCollectionRequestData);

    /**
     * Renames a collection.
     *
     * @param renameCollectionRequestData the request data for renaming a collection
     */
    void renameCollection(RenameCollectionRequestData renameCollectionRequestData);

    /**
     * Edits a collection.
     *
     * @param editCollectionRequestData the request data for editing a collection
     */
    void editCollection(EditCollectionRequestData editCollectionRequestData);
}
