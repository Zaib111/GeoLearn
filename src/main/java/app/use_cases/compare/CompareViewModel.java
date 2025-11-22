package app.use_cases.compare;

import app.views.ViewModel;
import app.views.compare.CompareState;

/**
 * ViewModel for Compare Countries feature.
 * Holds the UI state for the view and ensures updates trigger UI refresh.
 */
public class CompareViewModel extends ViewModel<CompareState> {

    public CompareViewModel() {
        super(new CompareState());
    }

    protected CompareState copyState(CompareState state) {
        return new CompareState(
                state.getCountryNames(),
                state.getColumnHeaders(),
                state.getComparisonTableData(),
                state.getSelectedCountries(),
                state.getErrorMessage()
        );
    }
}
