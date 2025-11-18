package use_case.compare;

import use_case.compare.CompareInputBoundary;
import use_case.compare.CompareInputData;

import java.util.List;

public class CompareController {

    private final CompareInputBoundary interactor;

    public CompareController(CompareInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Called by the view when the user clicks "Compare".
     */
    public void compare(List<String> countryCodes) {
        CompareInputData inputData = new CompareInputData(countryCodes);
        interactor.execute(inputData);
    }
}
