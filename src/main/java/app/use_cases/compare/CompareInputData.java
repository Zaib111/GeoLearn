package app.use_cases.compare;

import java.util.List;

/**
 * Data passed from the controller into the Compare use case.
 */
public class CompareInputData {

    private final List<String> countryCodes;

    public CompareInputData(List<String> countryCodes) {
        this.countryCodes = countryCodes;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }
}
