package app.use_cases.collection;

import app.entities.CountryCollection;

import java.util.List;
import java.util.UUID;

public class CollectionInteractor implements CollectionInputBoundary {
    private CollectionUserDataAccessInterface userDataAccessObject;
    private CollectionOutputBoundary collectionPresenter;

    public CollectionInteractor(CollectionUserDataAccessInterface userDataAccessObject, CollectionOutputBoundary collectionPresenter) {
        this.userDataAccessObject = userDataAccessObject;
        this.collectionPresenter = collectionPresenter;
    }

    @Override
    public void addCollection(AddCollectionRequestData collectionInputData) {
        CountryCollection newCollection = new CountryCollection(
                UUID.randomUUID(),
                collectionInputData.getCollectionName(),
                collectionInputData.getCountriesToAdd()
        );
        userDataAccessObject.createCollection(newCollection);
        List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(collections);
    }

    @Override
    public void fetchAllCollections() {
        List<CountryCollection> collections = userDataAccessObject.getAllCollections();
        collectionPresenter.prepareCollectionsView(collections);
    }
}
