package app.use_cases.compare;

import app.views.ViewModel;
import app.views.compare.CompareState;

public class CompareViewModel extends ViewModel<CompareState> {

    public CompareViewModel() {
        super(new CompareState());
    }
}
