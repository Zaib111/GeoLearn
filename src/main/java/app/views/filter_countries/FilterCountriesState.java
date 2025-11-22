package app.views.filter_countries;

import app.entities.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterCountriesState {
    private List<Country> filteredCountries = Collections.emptyList();
}
