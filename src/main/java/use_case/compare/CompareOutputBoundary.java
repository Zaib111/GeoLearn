package use_case.compare;

public interface CompareOutputBoundary {

    /**
     * Called when the comparison succeeds.
     */
    void prepareSuccessView(CompareOutputData outputData);

    /**
     * Called when the comparison fails (invalid selection, missing data, etc.).
     */
    void prepareFailView(String errorMessage);
}
