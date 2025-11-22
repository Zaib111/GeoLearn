package app.use_cases.explore_map;

/**
 * The output boundary for the Explore Map Use Case.
 * Simplified to only include data operations.
 */
public interface ExploreMapOutputBoundary {
    /**
     * Prepares the success view for loading the map.
     * @param outputData the output data
     */
    void prepareMapLoadedView(ExploreMapOutputData outputData);

    /**
     * Prepares the view after feature selection.
     * @param outputData the output data
     */
    void prepareFeatureSelectedView(ExploreMapOutputData outputData);

    /**
     * Prepares the failure view for the Explore Map Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
