package use_case.collection;

/**
 * The Collection Use Case.
 */
public interface CollectionInputBoundary {
    /**
     * Execute the Collection Use Case.
     * @param collectionInputData the input data for this use case
     */
    void execute(CollectionInputData collectionInputData);
}
