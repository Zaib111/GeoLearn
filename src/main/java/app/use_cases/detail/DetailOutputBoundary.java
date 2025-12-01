package app.use_cases.detail;

/**
 * Output Boundary interface for the Detail Use Case.
 * Defines the contract for the Presenter to handle the results of the Interactor's execution.
 * This ensures the Interactor remains independent of the View implementation.
 */
public interface DetailOutputBoundary {

    /**
     * Prepares the view when the use case fails (e.g., country not found).
     *
     * @param errorMessage A descriptive error message to be displayed.
     */
    void prepareDetailFailureView(String errorMessage);

    /**
     * Prepares the view when the use case succeeds.
     *
     * @param detailOutputData The output data containing the country details.
     */
    void prepareDetailSuccessView(DetailOutputData detailOutputData);
}