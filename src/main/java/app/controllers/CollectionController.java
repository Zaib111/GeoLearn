package app.controllers;

import java.util.List;
import java.util.UUID;

import app.use_cases.country_collection.CollectionAddInputData;
import app.use_cases.country_collection.CollectionInputBoundary;
import app.use_cases.country_collection.CollectionDeleteInputData;
import app.use_cases.country_collection.CollectionEditInputData;
import app.use_cases.country_collection.CollectionRenameInputData;

public class CollectionController {
    private final CollectionInputBoundary collectionUseCaseInteractor;

    /**
     * Constructs a CollectionController with the given use case interactor.
     *
     * @param collectionUseCaseInteractor the use case interactor for collections
     */
    public CollectionController(CollectionInputBoundary collectionUseCaseInteractor) {
        this.collectionUseCaseInteractor = collectionUseCaseInteractor;
    }

    /**
     * Adds a new collection with the specified name and countries.
     *
     * @param collectionName the name of the collection
     * @param countryNames the list of country names to include
     */
    public void addCollection(String collectionName, List<String> countryNames) {
        final CollectionAddInputData collectionInputData = new CollectionAddInputData(collectionName, countryNames);
        collectionUseCaseInteractor.addCollection(collectionInputData);
    }

    /**
     * Fetches all collections.
     */
    public void fetchAllCollections() {
        collectionUseCaseInteractor.fetchAllCollections();
    }

    /**
     * Deletes the collection with the specified ID.
     *
     * @param collectionId the UUID of the collection to delete
     */
    public void deleteCollection(UUID collectionId) {
        final CollectionDeleteInputData deleteRequestData = new CollectionDeleteInputData(collectionId);
        collectionUseCaseInteractor.deleteCollection(deleteRequestData);
    }

    /**
     * Renames the collection with the specified ID.
     *
     * @param collectionId the UUID of the collection to rename
     * @param newName the new name for the collection
     */
    public void renameCollection(UUID collectionId, String newName) {
        final CollectionRenameInputData renameRequestData = new CollectionRenameInputData(collectionId, newName);
        collectionUseCaseInteractor.renameCollection(renameRequestData);
    }

    /**
     * Edits the collection by adding and removing specified countries.
     *
     * @param collectionId the UUID of the collection to edit
     * @param countryNamesToAdd the list of country names to add
     * @param countryNamesToRemove the list of country names to remove
     */
    public void editCollection(UUID collectionId, List<String> countryNamesToAdd, List<String> countryNamesToRemove) {
        final CollectionEditInputData editRequestData = new CollectionEditInputData(
                collectionId,
                countryNamesToAdd,
                countryNamesToRemove
        );
        collectionUseCaseInteractor.editCollection(editRequestData);
    }
}
