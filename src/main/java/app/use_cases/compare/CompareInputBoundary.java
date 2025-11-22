package app.use_cases.compare;

import java.util.List;

public interface CompareInputBoundary {

    void loadAvailableCountries();

    void execute(List<String> selectedCountryNames);
}
