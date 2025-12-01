package app.views.filter_countries;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import org.jetbrains.annotations.NotNull;

import app.NavigationService;
import app.controllers.FilterCountriesController;
import app.entities.Country;
import app.views.AbstractView;
import app.views.ViewModel;

// View for when user wants to view table of sorted + filtered countries
public class FilterCountriesView extends AbstractView {
    private static final String ANY = "Any";
    private static final int TABLE_WIDTH = 750;
    private static final int TABLE_HEIGHT = 300;

    private JTextField searchField;
    private JComboBox<String> regionComboBox;
    private JComboBox<String> subregionComboBox;
    private FilterCountriesController filterCountriesController;
    private JScrollPane currentTableScrollPane;
    private NavigationService navigator;

    public FilterCountriesView(ViewModel<FilterCountriesState> filterCountriesViewModel,
                               FilterCountriesController filterCountriesController,
                               NavigationService navigator) {
        super(filterCountriesViewModel);

        this.filterCountriesController = filterCountriesController;
        this.navigator = navigator;

        // Heading
        final JLabel heading = new JLabel("Filter Countries");
        heading.setFont(new Font("Dialog", Font.BOLD, 24));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(heading);

        // Filter panel
        final JPanel filterPanel = new JPanel();
        filterPanel.setBorder(new EmptyBorder(40, 0, 20, 0));

        final Map<String, String[]> subregionMap = this.createSubregionMap();

        this.searchField = new JTextField(15);
        this.regionComboBox = new JComboBox<>(
                new String[]{ANY, "Africa", "Americas", "Antarctic", "Asia", "Europe", "Oceania"});
        this.regionComboBox.setPreferredSize(
                new Dimension(150, this.regionComboBox.getPreferredSize().height));
        this.subregionComboBox = new JComboBox<>();
        this.subregionComboBox.setPreferredSize(
                new Dimension(200, this.subregionComboBox.getPreferredSize().height));

        this.regionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterCountriesView.this.subregionComboBox.removeAllItems();
                final String currentRegion =
                        (String) FilterCountriesView.this.regionComboBox.getSelectedItem();
                for (String subregion : subregionMap.get(currentRegion)) {
                    FilterCountriesView.this.subregionComboBox.addItem(subregion);
                }
            }
        });

        this.regionComboBox.setSelectedIndex(0);
        this.regionComboBox.getActionListeners()[0].actionPerformed(null);

        filterPanel.add(new JLabel("Search Country"));
        filterPanel.add(this.searchField);
        filterPanel.add(new JLabel("Region"));
        filterPanel.add(this.regionComboBox);
        filterPanel.add(new JLabel("Subregion"));
        filterPanel.add(this.subregionComboBox);

        this.add(filterPanel);

        // Submit button
        final JButton submitButton = new JButton("Submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilterCountriesView.this.filterButtonClicked();
            }
        });

        this.add(submitButton);
    }

    @Override
    public void onViewOpened(String param) {
        this.revalidate();
        this.repaint();
    }

    @Override
    public void onViewClosed() {
        // nothing to do
    }

    /**
     * Called when the view model state changes.
     *
     * @param oldState the previous state
     * @param newState the new state
     */
    @Override
    public void onStateChange(Object oldState, Object newState) {
        final FilterCountriesState filterCountriesState = (FilterCountriesState) newState;
        this.displayCountries(filterCountriesState.getFilteredCountries());
    }

    private Map<String, String[]> createSubregionMap() {
        final Map<String, String[]> subregionMap = new HashMap<>();
        subregionMap.put(ANY, new String[]{ANY});
        subregionMap.put("Africa", new String[]{
            ANY, "Eastern Africa", "Middle Africa", "Northern Africa",
            "Southern Africa", "Western Africa"});
        subregionMap.put("Americas", new String[]{
            ANY, "North America", "Caribbean",
            "Central America", "South America"});
        subregionMap.put("Antarctic", new String[]{ANY});
        subregionMap.put("Asia", new String[]{
            ANY, "Central Asia", "Eastern Asia",
            "South-Eastern Asia", "Southern Asia", "Western Asia"});
        subregionMap.put("Europe", new String[]{
            ANY, "Central Europe", "Eastern Europe", "Northern Europe",
            "Southeast Europe", "Southern Europe", "Western Europe"});
        subregionMap.put("Oceania", new String[]{
            ANY, "Australia and New Zealand",
            "Melanesia", "Micronesia", "Polynesia"});

        return subregionMap;
    }

    private void filterButtonClicked() {
        final String search = this.searchField.getText();
        final String region = (String) this.regionComboBox.getSelectedItem();
        final String subregion = (String) this.subregionComboBox.getSelectedItem();

        this.filterCountriesController.filterCountries(search, region, subregion);
    }

    /**
     * Updates the display to show the given list of countries.
     *
     * @param countryDisplayData the list of countries to display
     */
    public void displayCountries(List<Country> countryDisplayData) {
        if (countryDisplayData.isEmpty()) {
            this.showNoResults();
        }
        else {
            this.showCountryTable(countryDisplayData);
        }
    }

    private void showNoResults() {
        if (this.currentTableScrollPane != null) {
            this.remove(this.currentTableScrollPane);
        }

        final JLabel noResults = new JLabel("No Results Found", SwingConstants.CENTER);
        noResults.setFont(new Font("Dialog", Font.PLAIN, 18));

        this.currentTableScrollPane = new JScrollPane(noResults);
        this.currentTableScrollPane.setPreferredSize(
                new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        this.currentTableScrollPane.setBorder(null);

        this.add(this.currentTableScrollPane);
        this.revalidate();
        this.repaint();
    }

    private void showCountryTable(List<Country> countryDisplayData) {
        final String[] columnNames = {
            "Name", "Region", "Subregion",
            "Population", "Area (km^2)", "Population Density", "Capital",
        };

        final Object[][] data = this.buildTableData(countryDisplayData);
        final JTable table = getFormattedTable(data, columnNames);

        this.configureSorting(table);
        this.addCountryClickListener(table, countryDisplayData);
        this.addCursorHoverListener(table);

        this.replaceTableScrollPane(table);

        this.revalidate();
        this.repaint();
    }

    private Object[][] buildTableData(List<Country> countryDisplayData) {
        final Object[][] data = new Object[countryDisplayData.size()][7];

        for (int i = 0; i < countryDisplayData.size(); i++) {
            final Country country = countryDisplayData.get(i);
            data[i][0] = country.getName();
            data[i][1] = country.getRegion();
            data[i][2] = country.getSubregion().orElse("N/A");
            data[i][3] = country.getPopulation();
            data[i][4] = country.getAreaKm2();
            data[i][5] = country.getPopulation() / country.getAreaKm2();
            data[i][6] = country.getCapital().orElse("N/A");
        }

        return data;
    }

    private void configureSorting(JTable table) {
        @SuppressWarnings("unchecked")
        final TableRowSorter<DefaultTableModel> sorter =
                (TableRowSorter<DefaultTableModel>) table.getRowSorter();
        final List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
    }

    private void replaceTableScrollPane(JTable table) {
        if (this.currentTableScrollPane != null) {
            this.remove(this.currentTableScrollPane);
        }

        this.currentTableScrollPane = new JScrollPane(table);
        this.currentTableScrollPane.setPreferredSize(
                new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
        this.add(this.currentTableScrollPane);
    }

    private void addCountryClickListener(final JTable table,
                                         final List<Country> countryDisplayData) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    final Point point = e.getPoint();
                    final int viewRow = table.rowAtPoint(point);

                    if (viewRow >= 0) {
                        final int modelRow = table.convertRowIndexToModel(viewRow);
                        final Country clickedCountry = countryDisplayData.get(modelRow);
                        final String countryCode = clickedCountry.getCode();
                        FilterCountriesView.this.navigator
                                .navigateTo("country_details", countryCode);
                    }
                }
            }
        });
    }

    private void addCursorHoverListener(final JTable table) {
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                final Point point = e.getPoint();
                final int viewRow = table.rowAtPoint(point);
                if (viewRow >= 0) {
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }

    @NotNull
    private static JTable getFormattedTable(Object[][] data, String[] columnNames) {
        final DefaultTableModel model = new CountryTableModel(data, columnNames);
        final JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        return table;
    }

    /**
     * Table model that provides specific column classes for sorting.
     */
    private static final class CountryTableModel extends DefaultTableModel {

        private static final Class<?>[] COLUMN_CLASSES = new Class<?>[]{
            // Name
            String.class,
            // Region
            String.class,
            // Subregion
            String.class,
            // Population
            Long.class,
            // Area
            Double.class,
            // Population Density
            Double.class,
            // Capital
            String.class,
        };

        CountryTableModel(Object[][] data, String[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            Class<?> result = Object.class;
            if (columnIndex >= 0 && columnIndex < COLUMN_CLASSES.length) {
                result = COLUMN_CLASSES[columnIndex];
            }
            return result;
        }
    }
}
