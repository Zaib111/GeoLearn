package app.controllers;

import app.use_cases.detail.DetailInputBoundary;
import app.use_cases.detail.DetailInputData;

/**
 * Controller responsible for requesting details from the Detail interactor.
 * Acts as a thin adapter between the UI/presenter layer and the use-case boundary.
 */
public class DetailController {

    private final DetailInputBoundary interactor;

    /**
     * Construct a DetailController with the given interactor.
     *
     * @param interactor the interactor implementing the detail input boundary
     */
    public DetailController(DetailInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Load details by forwarding the provided input data to the interactor.
     *
     * @param detailInputData input data required by the detail use case
     */
    public void loadDetails(DetailInputData detailInputData) {
        interactor.execute(detailInputData);
    }
}
