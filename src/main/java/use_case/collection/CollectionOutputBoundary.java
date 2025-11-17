package use_case.collection;

/**
 * The output boundary for the Collection Use Case.
 */
public interface CollectionOutputBoundary {
    /**
     * Prepares the success view for the Collection Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(CollectionOutputData outputData);

    /**
     * Prepares the failure view for the Collection Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
