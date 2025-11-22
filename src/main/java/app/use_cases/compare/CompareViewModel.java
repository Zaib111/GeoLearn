package app.use_cases.compare;

import java.util.ArrayList;
import java.util.List;

public class CompareViewModel {

    private List<String> selectedCountries = new ArrayList<>();
    private String errorMessage = "";
    private boolean hasError = false;

    public List<String> getSelectedCountries() {
        return selectedCountries;
    }

    public void setSelectedCountries(List<String> selectedCountries) {
        this.selectedCountries = selectedCountries;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return hasError;
    }

    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.hasError = (errorMessage != null && !errorMessage.isEmpty());
    }

    public void clearError() {
        this.errorMessage = "";
        this.hasError = false;
    }
}
