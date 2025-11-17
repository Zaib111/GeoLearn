package adapters.Collection;

import use_case.collection.CollectionOutputBoundary;
import use_case.collection.CollectionOutputData;
import adapters.ViewManagerModel;

public class CollectionPresenter implements CollectionOutputBoundary {
    private final CollectionViewModel collectionViewModel;
    private final ViewManagerModel viewManagerModel;

    public CollectionPresenter(ViewManagerModel viewManagerModel, CollectionViewModel collectionViewModel) {
        this.viewManagerModel = viewManagerModel;
        this.collectionViewModel = collectionViewModel;
    }

    @Override
    public void prepareSuccessView(CollectionOutputData outputData) {
        collectionViewModel.getState().setCollectionName(outputData.getCollectionName());
        collectionViewModel.getState().setCountries(outputData.getCountriesInCollection());
        collectionViewModel.getState().setError(null);
        collectionViewModel.firePropertyChange("collection");
    }

    @Override
    public void prepareFailView(String errorMessage) {
        collectionViewModel.getState().setError(errorMessage);
        collectionViewModel.firePropertyChange("collection");
    }
}