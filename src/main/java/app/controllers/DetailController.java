package app.controllers;

import app.use_cases.detail.DetailInputBoundary;
import app.use_cases.detail.DetailInputData;

/**
 * Controller responsible for requesting details from the Detail interactor.
 * Acts as a thin adapter between the UI/presenter layer and the use-case boundary.
 */
public class DetailController {

    // The interactor interface for the Detail use case
    private final DetailInputBoundary interactor;

    /**
     * Construct a DetailController with the given interactor.
     *
     * @param interactor The interactor implementing the detail input boundary.
     */
    public DetailController(DetailInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Load details by forwarding the provided input data to the interactor.
     *
     * @param detailInputData Input data required by the detail use case.
     */
    public void loadDetails(DetailInputData detailInputData) {
        // Delegate the execution of the use case to the interactor
        interactor.execute(detailInputData);
    }
}