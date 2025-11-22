package app.views.compare;

import app.entities.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompareState {

    private List<String> countryNames = Collections.emptyList();

    private String[] columnHeaders = new String[0];

    private Object[][] comparisonTableData = new Object[0][0];

    private List<Country> selectedCountries = Collections.emptyList();

    private String errorMessage = null;
}
