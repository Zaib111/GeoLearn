package app.use_cases.compare;

import java.util.List;

public interface CompareOutputBoundary {

    void prepareCountriesList(List<String> countryNames);

    void prepareSuccessView(CompareOutputData outputData);

    void prepareFailView(String errorMessage);
}
