package app.controllers;

import app.entities.Country;
import app.use_cases.collection.AddCollectionRequestData;
import app.use_cases.collection.CollectionInputBoundary;

import java.util.List;


public class CollectionController {
    private final CollectionInputBoundary collectionUseCaseInteractor;

    public CollectionController(CollectionInputBoundary collectionUseCaseInteractor) {
        this.collectionUseCaseInteractor = collectionUseCaseInteractor;
    }

    public void addCollection(String countryName, List<Country> countriesToAdd) {
        final AddCollectionRequestData collectionInputData = new AddCollectionRequestData(countryName, countriesToAdd);

        collectionUseCaseInteractor.addCollection(collectionInputData);
    }

    public void fetchAllCollections() {
        collectionUseCaseInteractor.fetchAllCollections();
    }
}
