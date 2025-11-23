package app.use_cases.filter_country;

import app.controllers.DetailController;
import app.data_access.APICountryDataAccessObject;
import app.entities.Country;
import app.presenters.DetailPresenter;
import app.use_cases.detail.DetailDataAccessInterface;
import app.use_cases.detail.DetailInteractor;
import app.views.ViewModel;
import app.views.detail.DetailState;
import app.views.detail.DetailView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FilterCountriesInteractor implements FilterCountriesInputBoundary {
    private final APICountryDataAccessObject dataAccess;
    private final FilterCountriesOutputBoundary presenter;
    private final DetailDataAccessInterface detailDataAccess; // For Detail use case

    public FilterCountriesInteractor(APICountryDataAccessObject dataAccess, FilterCountriesOutputBoundary presenter, DetailDataAccessInterface detailDataAccess) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
        this.detailDataAccess = detailDataAccess;
    }

    @Override
    public void filterCountries(FilterCountriesInputData inputData) {
        List<Country> allCountries = dataAccess.getCountries();
        List<Country> filteredCountries = new ArrayList<>();

        String searchTerm = inputData.getSearchTerm();
        String region = inputData.getRegion();
        String subregion = inputData.getSubregion();

        for (Country country : allCountries) {
            if (matchesSearch(country, searchTerm) && matchesRegion(country, region) && matchesSubregion(country, subregion)) {
                filteredCountries.add(country);
            }
        }

        FilterCountriesOutputData outputData = new FilterCountriesOutputData(filteredCountries);

        presenter.presentFilteredCountries(outputData);
    }

    private static boolean matchesSubregion(Country country, String subregion) {
        return subregion.equals(country.getSubregion().orElse(null)) || subregion.equals("Any");
    }

    private static boolean matchesRegion(Country country, String region) {
        return region.equals(country.getRegion()) || region.equals("Any");
    }

    private boolean matchesSearch(Country country, String searchTerm) {
        return country.getName().toLowerCase().contains(searchTerm.toLowerCase());
    }

    /**
     * Creates and opens a new window (JFrame) with the DetailView for a specific country.
     * * @param countryName The name of the country clicked by the user.
     */
    @Override
    public void openCountryDetails(String countryName) {
        // Instantiates the Detail architecture
        ViewModel<DetailState> detailViewModel = new ViewModel<>(new DetailState());
        DetailPresenter detailPresenter = new DetailPresenter(detailViewModel);
        DetailInteractor detailInteractor = new DetailInteractor(detailDataAccess, detailPresenter);
        DetailController detailController = new DetailController(detailInteractor);
        DetailView detailView = new DetailView(detailViewModel, detailController, countryName);


        // Setup frame with desired properties, displaying the details of the clicked Country
        JFrame detailFrame = new JFrame("Details for " + countryName);
        detailFrame.setSize(800, 600);
        detailFrame.setResizable(false);
        detailFrame.setLayout(new BorderLayout());
        detailFrame.add(detailView, BorderLayout.CENTER);

        detailFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        detailFrame.setLocationRelativeTo(null);
        detailFrame.setVisible(true);

        detailView.onViewOpened();
    }
}