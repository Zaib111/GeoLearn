package app.use_cases.detail;

/**
 * Output boundary for detail use case.
 */
public interface DetailOutputBoundary {

    /**
     * Prepare the failure view.
     * @param errorMessage the error message
     */
    void prepareDetailFailureView(String errorMessage);

    /**
     * Prepare the success view.
     * @param detailOutputData the output data
     */
    void prepareDetailSuccessView(DetailOutputData detailOutputData);
}
