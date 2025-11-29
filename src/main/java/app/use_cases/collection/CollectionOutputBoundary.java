package app.use_cases.collection;

/**
 * Output boundary for presenting country collections and errors.
 */
public interface CollectionOutputBoundary {
    /**
     * Prepares the view for displaying a list of country collections.
     *
     * @param outputData the output data containing the collections to display
     */
    void prepareCollectionsView(CollectionOutputData outputData);

    /**
     * Prepares the view for displaying an error message.
     *
     * @param errorMessage the error message to display
     */
    void prepareErrorView(String errorMessage);
}
