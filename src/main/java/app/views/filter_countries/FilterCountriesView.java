package app.views.filter_countries;

import app.controllers.FilterCountriesController;
import app.entities.Country;
import app.views.AbstractView;
import app.views.ViewModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
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
        regionComboBox = new JComboBox<>(new String[]{"Any", "Africa", "Americas", "Antarctic", "Asia", "Europe", "Oceania"});
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
        subregionHashMap.put("Africa", new String[]{"Any", "Eastern Africa", "Middle Africa", "Northern Africa", "Southern Africa", "Western Africa"});
        subregionHashMap.put("Americas", new String[]{"Any", "Northern America", "Caribbean", "Central America", "South America"});
        subregionHashMap.put("Antarctic", new String[]{"Any"});
        subregionHashMap.put("Asia", new String[]{"Any", "Central Asia", "Eastern Asia", "South-eastern Asia", "Southern Asia", "Western Asia"});
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
        String[] columnNames = {"Name", "Region", "Subregion", "Population", "Area (km²)", "Population Density", "Capital"};

        Object[][] data = new Object[countryDisplayData.size()][7];

        for (int i = 0; i < countryDisplayData.size(); i++) {
            Country country = countryDisplayData.get(i);
            data[i][0] = country.getName();
            data[i][1] = country.getRegion();
            data[i][2] = country.getSubregion().orElse("N/A");
            data[i][3] = country.getPopulation();
            data[i][4] = country.getAreaKm2();
            data[i][5] = country.getPopulation() / country.getAreaKm2();
            data[i][6] = country.getCapital().orElse("N/A");
        }

        JTable table = getFormattedTable(data, columnNames);

        TableRowSorter<DefaultTableModel> sorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();

        // Remove old table pane if exists
        if (currentTableScrollPane != null) {
            this.remove(currentTableScrollPane); // panel must be a class field
        }

        // Add new table pane
        currentTableScrollPane = new JScrollPane(table);
        currentTableScrollPane.setPreferredSize(new Dimension(750, 300));
        this.add(currentTableScrollPane);

        // Refresh display
        this.revalidate();
        this.repaint();
    }

    @NotNull
    private static JTable getFormattedTable(Object[][] data, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch(columnIndex) {
                    case 0: return String.class;  // Name
                    case 1: return String.class;  // Region
                    case 2: return String.class;  // Subregion
                    case 3: return Long.class;    // Population
                    case 4: return Double.class;  // Area
                    case 5: return Double.class;  // Population Density
                    case 6: return String.class; // Capital
                    default: return Object.class;
                }
            }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        return table;
    }


}