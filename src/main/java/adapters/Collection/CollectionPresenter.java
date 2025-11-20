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
        collectionViewModel.getState().setCountriesToAdd(outputData.getCountriesInCollection());
        collectionViewModel.getState().setCollectionError(null);
        collectionViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        collectionViewModel.getState().setCollectionError(errorMessage);
        collectionViewModel.firePropertyChange();
    }
}
