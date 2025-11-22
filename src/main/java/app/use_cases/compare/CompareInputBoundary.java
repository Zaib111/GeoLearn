package app.use_cases.compare;

import java.util.List;

/**
 * Input boundary for the Compare Countries use case.
 * Implemented by the CompareInteractor and called by the CompareController.
 */
public interface CompareInputBoundary {

    /**
     * Execute the compare use case for the given list of selected country names.
     *
     * @param selectedCountryNames names of the countries selected by the user,
     *                             in the order they should be compared
     */
    void execute(List<String> selectedCountryNames);
}
