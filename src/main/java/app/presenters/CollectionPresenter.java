package app.presenters;

import app.use_cases.country_collection.CollectionOutputBoundary;
import app.use_cases.country_collection.CollectionOutputData;
import app.views.ViewModel;
import app.views.country_collection.CollectionState;

import javax.swing.*;

public class CollectionPresenter implements CollectionOutputBoundary {
    private final ViewModel<CollectionState> collectionViewModel;

    public CollectionPresenter(ViewModel<CollectionState> collectionViewModel) {
        this.collectionViewModel = collectionViewModel;
    }

    @Override
    public void prepareCollectionsView(CollectionOutputData outputData) {
        CollectionState state = collectionViewModel.getState();
        state.setAllCollections(outputData.getCollections());
        state.setErrorMessage(null);
        collectionViewModel.updateState(state);
    }

    @Override
    public void prepareErrorView(String errorMessage) {
        CollectionState state = collectionViewModel.getState();
        state.setErrorMessage(errorMessage);
        collectionViewModel.updateState(state);
        // Show error dialog to user
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });
    }
}