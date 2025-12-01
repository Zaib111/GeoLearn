package app.use_cases.country_collection;

/**
 * Input boundary interface for collection-related use cases.
 * Defines methods for adding, fetching, deleting, renaming, and editing collections.
 */
public interface CollectionInputBoundary {
    /**
     * Adds a new collection.
     *
     * @param collectionAddInputData the request data for adding a collection
     */
    void addCollection(CollectionAddInputData collectionAddInputData);

    /**
     * Fetches all collections.
     */
    void fetchAllCollections();

    /**
     * Deletes a collection.
     *
     * @param collectionDeleteInputData the request data for deleting a collection
     */
    void deleteCollection(CollectionDeleteInputData collectionDeleteInputData);

    /**
     * Renames a collection.
     *
     * @param collectionRenameInputData the request data for renaming a collection
     */
    void renameCollection(CollectionRenameInputData collectionRenameInputData);

    /**
     * Edits a collection.
     *
     * @param collectionEditInputData the request data for editing a collection
     */
    void editCollection(CollectionEditInputData collectionEditInputData);
}
