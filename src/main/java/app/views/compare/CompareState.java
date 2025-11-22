package app.views.compare;

import app.entities.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * UI state for the Compare Countries view.
 * <p>
 * This is updated by the presenter and read by the view.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompareState {

    /**
     * The list of countries selected by the user for comparison, in the order chosen.
     */
    private List<Country> selectedCountries = Collections.emptyList();

    /**
     * Table data representing country comparison attributes.
     */
    private Object[][] comparisonTableData = new Object[0][0];

    /**
     * Column headers for comparisonTableData.
     */
    private String[] columnHeaders = new String[0];

    /**
     * Error message to show to the user if something goes wrong.
     */
    private String errorMessage;
}
