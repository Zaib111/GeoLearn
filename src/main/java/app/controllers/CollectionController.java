package app.controllers;

import app.use_cases.collection.*;

import java.util.List;
import java.util.UUID;


public class CollectionController {
    private final CollectionInputBoundary collectionUseCaseInteractor;

    public CollectionController(CollectionInputBoundary collectionUseCaseInteractor) {
        this.collectionUseCaseInteractor = collectionUseCaseInteractor;
    }

    public void addCollection(String collectionName, List<String> countryNames) {
        final AddCollectionRequestData collectionInputData = new AddCollectionRequestData(collectionName, countryNames);
        collectionUseCaseInteractor.addCollection(collectionInputData);
    }

    public void fetchAllCollections() {
        collectionUseCaseInteractor.fetchAllCollections();
    }

    public void deleteCollection(UUID collectionId) {
        final DeleteCollectionRequestData deleteRequestData = new DeleteCollectionRequestData(collectionId);
        collectionUseCaseInteractor.deleteCollection(deleteRequestData);
    }

    public void renameCollection(UUID collectionId, String newName) {
        final RenameCollectionRequestData renameRequestData = new RenameCollectionRequestData(collectionId, newName);
        collectionUseCaseInteractor.renameCollection(renameRequestData);
    }

    public void editCollection(UUID collectionId, List<String> countryNamesToAdd, List<String> countryNamesToRemove) {
        final EditCollectionRequestData editRequestData = new EditCollectionRequestData(
                collectionId,
                countryNamesToAdd,
                countryNamesToRemove
        );
        collectionUseCaseInteractor.editCollection(editRequestData);
    }
}
