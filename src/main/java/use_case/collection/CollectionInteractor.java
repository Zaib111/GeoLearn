package use_case.collection;

import entity.CountryCollection;

import java.util.UUID;

public class CollectionInteractor implements CollectionInputBoundary {
    private CollectionUserDataAccessInterface userDataAccessObject;
    private CollectionOutputBoundary collectionPresenter;

    public CollectionInteractor(CollectionUserDataAccessInterface userDataAccessObject, CollectionOutputBoundary collectionPresenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.collectionPresenter = collectionPresenter;
    }

    @Override
    public void execute(CollectionInputData collectionInputData) {
        boolean exists = userDataAccessObject.getAllCollections().stream()
                .anyMatch(collection -> collection.getCollectionName().equals(collectionInputData.getCollectionName()));
        if (exists) {
            collectionPresenter.prepareFailView("Collection with name \"" + collectionInputData.getCollectionName() + "\" already exists.");
        }
        else {
            final CountryCollection newCollection = new CountryCollection(
                    UUID.randomUUID(),
                    collectionInputData.getCollectionName(),
                    collectionInputData.getCountriesToAdd()
            );
            userDataAccessObject.createCollection(newCollection);

            final CollectionOutputData collectionOutputData = new CollectionOutputData(
                    newCollection.getCollectionName(),
                    newCollection.getCountries()
            );
            collectionPresenter.prepareSuccessView(collectionOutputData);
        }
    }
}
