package app;

import adapters.FilterCountriesController;
import adapters.FilterCountriesPresenter;
import data_access.APICountryDataAccessObject;
import use_case.filter_country.FilterCountriesInteractor;
import view.CountryTableView;

import javax.swing.*;

public class Main2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CountryTableView view = new CountryTableView();

            // Step 2: Create the presenter (depends on view)
            FilterCountriesPresenter presenter = new FilterCountriesPresenter(view);

            // Step 3: Create the data access object
            APICountryDataAccessObject dataAccess = new APICountryDataAccessObject();

            // Step 4: Create the interactor (depends on data access and presenter)
            FilterCountriesInteractor interactor =
                    new FilterCountriesInteractor(dataAccess, presenter);

            // Step 5: Create the controller (depends on interactor)
            FilterCountriesController controller =
                    new FilterCountriesController(interactor);

            // Step 6: Connect controller to view
            view.setController(controller);
        });
    }
}
