package app.views.filter_countries;

import app.controllers.FilterCountriesController;
import app.entities.Country;
import app.views.AbstractView;
import app.views.ViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;

// View for when user wants to view table of sorted + filtered countries
public class FilterCountriesView extends AbstractView {
    private JTextField searchField;
    private JComboBox<String> regionComboBox;
    private JComboBox<String> subregionComboBox;
    private FilterCountriesController filterCountriesController;
    private JScrollPane currentTableScrollPane;

    public FilterCountriesView(ViewModel<FilterCountriesState> filterCountriesViewModel, FilterCountriesController filterCountriesController) {
        super(filterCountriesViewModel);

        this.filterCountriesController = filterCountriesController;

        // Heading
        JLabel heading = new JLabel("Filter Countries");
        heading.setFont(new Font("Dialog", Font.BOLD, 24));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(heading);

        // Filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(new EmptyBorder(40, 0, 20, 0));

        HashMap<String, String[]> subregionHashMap = createSubregionHashMap();

        searchField = new JTextField(15);
        regionComboBox = new JComboBox<>(new String[]{"Any", "Asia", "North America", "South America", "Africa", "Europe", "Oceania"});
        regionComboBox.setPreferredSize(new Dimension(150, regionComboBox.getPreferredSize().height));
        subregionComboBox = new JComboBox<>();
        subregionComboBox.setPreferredSize(new Dimension(200, subregionComboBox.getPreferredSize().height));


        regionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                subregionComboBox.removeAllItems();
                String currentRegion = regionComboBox.getSelectedItem().toString();
                for (String subregion : subregionHashMap.get(currentRegion)) {
                    subregionComboBox.addItem(subregion);
                }
            }
        });

        regionComboBox.setSelectedIndex(0);
        regionComboBox.getActionListeners()[0].actionPerformed(null);

        filterPanel.add(new JLabel("Search Country"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Region"));
        filterPanel.add(regionComboBox);
        filterPanel.add(new JLabel("Subregion"));
        filterPanel.add(subregionComboBox);

        this.add(filterPanel);

        // Submit button
        JButton submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                filterButtonClicked();
            }
        });

        this.add(submitButton);
    }

    @Override
    public void onViewOpened() {
//        filterCountriesController.filterCountries("", "Any", "Any");
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onViewClosed() {

    }

    public void onStateChange(Object oldState, Object newState) {
        FilterCountriesState filterCountriesState = (FilterCountriesState) newState;

        displayCountries(filterCountriesState.getFilteredCountries());
    }

    private HashMap<String, String[]> createSubregionHashMap(){
        HashMap<String, String[]> subregionHashMap = new HashMap<>();
        subregionHashMap.put("Any", new String[]{"Any"});
        subregionHashMap.put("Asia", new String[]{"Any", "Central Asia", "Eastern Asia", "South-eastern Asia", "Southern Asia", "Western Asia"});
        subregionHashMap.put("North America", new String[]{"Any", "Northern America", "Central America", "Caribbean"});
        subregionHashMap.put("South America", new String[]{"Any"});
        subregionHashMap.put("Africa", new String[]{"Any", "Eastern Africa", "Middle Africa", "Northern Africa", "Southern Africa", "Western Africa"});
        subregionHashMap.put("Europe", new String[]{"Any", "Eastern Europe", "Northern Europe", "Southern Europe", "Western Europe"});
        subregionHashMap.put("Oceania", new String[]{"Any", "Australia and New Zealand", "Melanesia", "Micronesia", "Polynesia"});

        return subregionHashMap;
    }

    private void filterButtonClicked() {
        String search = searchField.getText();
        String region = (String) regionComboBox.getSelectedItem();
        String subregion = (String) subregionComboBox.getSelectedItem();

        filterCountriesController.filterCountries(search, region, subregion);

    }

    public void displayCountries(List<Country> countryDisplayData) {
        String[] columnNames = {"Name", "Region", "Subregion", "Population", "Area (km²)", "Population Density"};

        Object[][] data = new Object[countryDisplayData.size()][6];

        for (int i = 0; i < countryDisplayData.size(); i++) {
            Country country = countryDisplayData.get(i);
            data[i][0] = country.getName();
            data[i][1] = country.getRegion();
            data[i][2] = country.getSubregion().orElse("");
            data[i][3] = String.format("%,d", country.getPopulation());
            data[i][4] = String.format("%,.2f", country.getAreaKm2());
            data[i][5] = String.format("%.2f", country.getPopulation() / country.getAreaKm2());
        }

        JTable table = new JTable(data, columnNames);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        // Remove old table if exists
        if (currentTableScrollPane != null) {
            this.remove(currentTableScrollPane); // panel must be a class field
        }

        // Add new table
        currentTableScrollPane = new JScrollPane(table);
        currentTableScrollPane.setPreferredSize(new Dimension(750, 300));
        this.add(currentTableScrollPane);

        // Refresh display
        this.revalidate();
        this.repaint();
    }


}
