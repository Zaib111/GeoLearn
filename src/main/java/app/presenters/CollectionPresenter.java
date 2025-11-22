package app.presenters;

import app.entities.CountryCollection;
import app.use_cases.collection.CollectionOutputBoundary;
import app.views.ViewModel;
import app.views.collection.CollectionState;

import javax.swing.*;
import java.util.List;

public class CollectionPresenter implements CollectionOutputBoundary {
    private final ViewModel<CollectionState> collectionViewModel;

    public CollectionPresenter(ViewModel<CollectionState> collectionViewModel) {
        this.collectionViewModel = collectionViewModel;
    }

    @Override
    public void prepareCollectionsView(List<CountryCollection> collections) {
        CollectionState state = collectionViewModel.getState();
        state.setAllCollections(collections);
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