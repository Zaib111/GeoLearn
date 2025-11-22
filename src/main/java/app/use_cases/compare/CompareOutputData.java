package app.use_cases.compare;

import app.entities.Country;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class CompareOutputData {

    private final List<Country> selectedCountries;

    public CompareOutputData(List<Country> selectedCountries) {
        this.selectedCountries = Collections.unmodifiableList(selectedCountries);
    }

}
