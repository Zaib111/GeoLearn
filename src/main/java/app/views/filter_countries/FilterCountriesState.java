package app.views.filter_countries;

import java.util.Collections;
import java.util.List;

import app.entities.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCountriesState {
    private List<Country> filteredCountries = Collections.emptyList();
    // no need for getters/setters as we use lombok
}
