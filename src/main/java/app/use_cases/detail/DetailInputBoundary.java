package app.use_cases.detail;

/**
 * Input Boundary interface for the Detail Use Case.
 * Defines the contract for the Interactor, allowing the Controller to trigger the use case execution.
 * This acts as an abstraction layer between the Interface Adapters and the Application Business Rules.
 */
public interface DetailInputBoundary {

    /**
     * Executes the business logic for retrieving country details.
     *
     * @param detailInputData The input data containing the country identifier.
     */
    void execute(DetailInputData detailInputData);
}