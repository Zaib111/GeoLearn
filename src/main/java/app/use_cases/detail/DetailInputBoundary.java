package app.use_cases.detail;

/**
 * Input Boundary for actions involving the detailed view page.
 */

public interface DetailInputBoundary {

    /**
     * Execute the detail use case.
     * @param detailInputData the input data
     */
    void execute(DetailInputData detailInputData);
}
