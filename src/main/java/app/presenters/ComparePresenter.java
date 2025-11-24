package app.presenters;

import app.entities.Country;
import app.use_cases.compare.CompareOutputBoundary;
import app.use_cases.compare.CompareOutputData;
import app.views.compare.CompareState;
import app.use_cases.compare.CompareViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Presenter for the Compare Countries use case.
 * Converts business logic output into UI state and updates the ViewModel.
 */
public class ComparePresenter implements CompareOutputBoundary {

    private final CompareViewModel viewModel;

    public ComparePresenter(CompareViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareCountriesList(List<String> countryNames) {
        // Make a sorted copy so dropdowns are alphabetical
        List<String> sortedNames = new ArrayList<>(countryNames);
        sortedNames.sort(String::compareToIgnoreCase);

        // When we're just loading the country list (e.g. on view open),
        // we DO NOT want to re-show the previous comparison popup.
        // So we clear comparison-related fields here.
        CompareState oldState = viewModel.getState();

        CompareState newState = new CompareState(
                sortedNames,                          // countryNames (sorted)
                new String[0],                        // clear comparison headers
                new Object[0][0],                     // clear comparison table
                Collections.emptyList(),              // clear selected countries
                null                                  // clear error
        );

        viewModel.updateState(newState);
    }

    @Override
    public void prepareSuccessView(CompareOutputData outputData) {
        List<Country> countries = outputData.getSelectedCountries();
        int num = countries.size();

        List<String> colHeaders = new ArrayList<>();
        colHeaders.add("Attribute");
        for (Country c : countries) {
            colHeaders.add(c.getName());
        }

        String[] attributes = {
                "Name", "Capital", "Region", "Subregion",
                "Population", "Area (km²)", "Density (people/km²)",
                "Languages", "Currencies"
        };

        Object[][] table = new Object[attributes.length][num + 1];

        for (int r = 0; r < attributes.length; r++) {
            table[r][0] = attributes[r];

            for (int c = 0; c < num; c++) {
                Country country = countries.get(c);
                Object val;

                String attr = attributes[r];
                switch (attr) {
                    case "Name":
                        val = country.getName();
                        break;
                    case "Capital":
                        val = country.getCapital().orElse("N/A");
                        break;
                    case "Region":
                        val = country.getRegion();
                        break;
                    case "Subregion":
                        val = country.getSubregion().orElse("N/A");
                        break;
                    case "Population":
                        val = country.getPopulation();
                        break;
                    case "Area (km²)":
                        val = String.format("%.2f", country.getAreaKm2());
                        break;
                    case "Density (people/km²)":
                        double area = country.getAreaKm2();
                        long pop = country.getPopulation();
                        double density = area > 0 ? (double) pop / area : 0.0;
                        val = String.format("%.2f", density);
                        break;
                    case "Languages":
                        val = String.join(", ", country.getLanguages());
                        break;
                    case "Currencies":
                        val = String.join(", ", country.getCurrencies());
                        break;
                    default:
                        val = "";
                }

                table[r][c + 1] = val;
            }
        }

        // Keep the country list, replace comparison data
        CompareState oldState = viewModel.getState();

        CompareState newState = new CompareState(
                oldState.getCountryNames(),               // keep full country list
                colHeaders.toArray(new String[0]),        // new headers
                table,                                    // new comparison table
                countries,                                // new selected countries
                null                                      // clear error
        );

        viewModel.updateState(newState);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        CompareState oldState = viewModel.getState();

        CompareState newState = new CompareState(
                oldState.getCountryNames(),               // keep country list
                oldState.getColumnHeaders(),              // keep headers
                oldState.getComparisonTableData(),        // keep table
                oldState.getSelectedCountries(),          // keep selected countries
                errorMessage                              // set error
        );

        viewModel.updateState(newState);
    }
}