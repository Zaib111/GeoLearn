package app.presenters;

import app.entities.Country;
import app.use_cases.compare.CompareOutputBoundary;
import app.use_cases.compare.CompareOutputData;
import app.views.compare.CompareState;
import app.use_cases.compare.CompareViewModel;

import java.util.ArrayList;
import java.util.List;

public class ComparePresenter implements CompareOutputBoundary {

    private final CompareViewModel viewModel;

    public ComparePresenter(CompareViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void prepareSuccessView(CompareOutputData outputData) {
        CompareState state = viewModel.getState();
        List<Country> selectedCountries = outputData.getSelectedCountries();
        int numCountries = selectedCountries.size();

        String[] columnHeaders = new String[numCountries + 1];
        columnHeaders[0] = "Attribute";

        for (int i = 0; i < numCountries; i++) {
            columnHeaders[i + 1] = safeString(selectedCountries.get(i).getName());
        }

        String[] attributes = {
                "Name",
                "Capital",
                "Region",
                "Subregion",
                "Population",
                "Area (km²)",
                "Density (people/km²)",
                "Languages",
                "Currencies"
        };

        int rows = attributes.length;
        Object[][] tableData = new Object[rows][numCountries + 1];

        for (int r = 0; r < rows; r++) {
            tableData[r][0] = attributes[r];

            for (int c = 0; c < numCountries; c++) {
                Country country = selectedCountries.get(c);
                Object value;

                switch (attributes[r]) {
                    case "Name":
                        value = safeString(country.getName());
                        break;
                    case "Capital":
                        value = country.getCapital().orElse("N/A");
                        break;
                    case "Region":
                        value = safeString(country.getRegion());
                        break;
                    case "Subregion":
                        value = country.getSubregion().orElse("N/A");
                        break;
                    case "Population":
                        value = country.getPopulation();
                        break;
                    case "Area (km²)":
                        value = String.format("%.2f", country.getAreaKm2());
                        break;
                    case "Density (people/km²)":
                        double density = country.getAreaKm2() > 0 ?
                                (double) country.getPopulation() / country.getAreaKm2() : 0.0;
                        value = String.format("%.2f", density);
                        break;
                    case "Languages":
                        value = listCSV(country.getLanguages());
                        break;
                    case "Currencies":
                        value = listCSV(country.getCurrencies());
                        break;
                    default:
                        value = "";
                }

                tableData[r][c + 1] = value;
            }
        }

        state.setSelectedCountries(selectedCountries);
        state.setColumnHeaders(columnHeaders);
        state.setComparisonTableData(tableData);
        state.setErrorMessage(null);
    }

    @Override
    public void prepareFailView(String errorMessage) {
        CompareState state = viewModel.getState();

        state.setSelectedCountries(new ArrayList<>());
        state.setColumnHeaders(new String[0]);
        state.setComparisonTableData(new Object[0][0]);
        state.setErrorMessage(errorMessage);
    }

    private String safeString(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private String listCSV(List<String> list) {
        return (list == null || list.isEmpty()) ? "N/A" : String.join(", ", list);
    }
}
