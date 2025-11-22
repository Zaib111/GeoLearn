package app.use_cases.compare;

public interface CompareOutputBoundary {

    /**
     * Called when comparison is successful.
     */
    void prepareSuccessView(CompareOutputData outputData);

    /**
     * Called when validation or data retrieval fails.
     */
    void prepareFailView(String errorMessage);
}
