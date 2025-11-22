package app.use_cases.compare;

import app.entities.Country;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CompareOutputData {

    /**
     * The list of countries being compared,
     * in the order the user selected them.
     */
    private List<Country> selectedCountries;
}
