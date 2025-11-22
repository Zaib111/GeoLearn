package app.use_cases.collection;

import java.util.List;

import app.entities.CountryCollection;

/**
 * Output boundary for presenting country collections and errors.
 */
public interface CollectionOutputBoundary {
    /**
     * Prepares the view for displaying a list of country collections.
     *
     * @param collections the list of country collections to display
     */
    void prepareCollectionsView(List<CountryCollection> collections);

    /**
     * Prepares the view for displaying an error message.
     *
     * @param errorMessage the error message to display
     */
    void prepareErrorView(String errorMessage);
}
