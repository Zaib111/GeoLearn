package app.use_cases.compare;

import java.util.List;

/**
 * Output boundary for the Compare Countries use case.
 * Defines methods for presenting country lists, success, and failure views to the user interface.
 */
public interface CompareOutputBoundary {
    /**
     * Prepares the view with a list of available country names for comparison.
     * @param countryNames the list of country names
     */
    void prepareCountriesList(List<String> countryNames);

    /**
     * Prepares the view with the results of a successful comparison.
     * @param outputData the output data containing comparison results
     */
    void prepareSuccessView(CompareOutputData outputData);

    /**
     * Prepares the view with an error message when comparison fails.
     * @param errorMessage the error message to display
     */
    void prepareFailView(String errorMessage);
}
