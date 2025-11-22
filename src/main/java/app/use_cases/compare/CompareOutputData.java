package app.use_cases.compare;

import java.util.Collections;
import java.util.List;

import app.entities.Country;
import lombok.Getter;

@Getter
public class CompareOutputData {

    private final List<Country> selectedCountries;

    public CompareOutputData(List<Country> selectedCountries) {
        this.selectedCountries = Collections.unmodifiableList(selectedCountries);
    }

}
