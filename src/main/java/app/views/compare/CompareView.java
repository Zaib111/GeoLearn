package app.views.compare;

import app.Navigator;
import app.controllers.CompareController;
import app.entities.Country;
import app.data_access.APICountryDataAccessObject;
import app.use_cases.compare.CompareViewModel;
import app.views.AbstractView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.List;

public class CompareView extends AbstractView {

    private final CompareViewModel viewModel;
    private final CompareController compareController;
    private final Navigator navigator;

    public CompareView(CompareViewModel viewModel,
                       CompareController compareController,
                       Navigator navigator) {
        super(viewModel);
        this.viewModel = viewModel;
        this.compareController = compareController;
        this.navigator = navigator;

        buildUI();
    }

    private void buildUI() {
        APICountryDataAccessObject dao = new APICountryDataAccessObject();
        List<Country> countries = dao.getCountries();

        if (countries == null || countries.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not load countries from API.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Map<String, Country> countryByName = new HashMap<>();
        for (Country c : countries) {
            countryByName.put(c.getName(), c);
        }

        String[] countryOptions = countryByName.keySet().toArray(new String[0]);
        Arrays.sort(countryOptions);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel heading = new JLabel("Compare Countries");
        heading.setFont(new Font("Dialog", Font.BOLD, 26));
        heading.setAlignmentX(CENTER_ALIGNMENT);
        add(heading);
        add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel countPanel = new JPanel();
        JLabel countLabel = new JLabel("Number of countries to compare:");
        Integer[] countChoices = {2, 3, 4, 5};
        JComboBox<Integer> countComboBox = new JComboBox<>(countChoices);
        countComboBox.setSelectedItem(2);
        countPanel.add(countLabel);
        countPanel.add(countComboBox);
        add(countPanel);

        JPanel countriesPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        @SuppressWarnings("unchecked")
        JComboBox<String>[] dropdowns = new JComboBox[5];

        for (int i = 0; i < 5; i++) {
            JLabel label = new JLabel("Country " + (i + 1) + ":");
            JComboBox<String> comboBox = new JComboBox<>(countryOptions);
            dropdowns[i] = comboBox;
            countriesPanel.add(label);
            countriesPanel.add(comboBox);
        }
        add(countriesPanel);

        ActionListener updateVisible = e -> {
            int count = (Integer) countComboBox.getSelectedItem();
            for (int i = 0; i < dropdowns.length; i++) {
                dropdowns[i].setEnabled(i < count);
            }
        };
        updateVisible.actionPerformed(null);
        countComboBox.addActionListener(updateVisible);

        add(Box.createRigidArea(new Dimension(0, 20)));

        JButton compareButton = new JButton("Compare Countries");
        compareButton.setAlignmentX(CENTER_ALIGNMENT);
        add(compareButton);

        compareButton.addActionListener(e -> {
            int count = (Integer) countComboBox.getSelectedItem();
            List<Country> selected = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String name = (String) dropdowns[i].getSelectedItem();
                selected.add(countryByName.get(name));
            }

            if (new HashSet<>(selected).size() < selected.size()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Each country must be unique.",
                        "Duplicate",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selected.size() < 2) {
                JOptionPane.showMessageDialog(this, "Pick at least two countries.");
                return;
            }

            showComparisonWindow(selected);
        });
    }

    @Override
    public void onViewOpened() {
        // Optional: could refresh dropdowns if state changes
    }

    @Override
    public void onViewClosed() {
        // Optional: clear state if needed
    }

    @Override
    public void onStateChange(Object oldState, Object newState) {
        // Future: update UI when CompareState updates
    }


    // ------------------ COMPARISON WINDOW ------------------
    private static void showComparisonWindow(List<Country> selectedCountries) {
        int numCountries = selectedCountries.size();

        JPanel flagsPanel = new JPanel(new GridLayout(1, numCountries + 1, 10, 10));
        flagsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        flagsPanel.add(new JPanel()); // placeholder

        for (Country c : selectedCountries) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            ImageIcon flag = loadFlag(c, 140, 90);
            JLabel img = (flag != null
                    ? new JLabel(flag)
                    : new JLabel("No Flag Found", SwingConstants.CENTER));

            img.setAlignmentX(CENTER_ALIGNMENT);
            card.add(img);

            JLabel name = new JLabel(c.getName());
            name.setFont(new Font("Dialog", Font.BOLD, 14));
            name.setAlignmentX(CENTER_ALIGNMENT);
            card.add(Box.createRigidArea(new Dimension(0, 8)));
            card.add(name);

            flagsPanel.add(card);
        }

        String[] attributes = {
                "Name", "Capital", "Region", "Subregion",
                "Population", "Area (km²)", "Density (people/km²)",
                "Languages", "Currencies"
        };

        Object[][] data = new Object[attributes.length][numCountries + 1];
        String[] colNames = new String[numCountries + 1];
        colNames[0] = "Attribute";

        for (int c = 0; c < numCountries; c++)
            colNames[c + 1] = selectedCountries.get(c).getName();

        for (int r = 0; r < attributes.length; r++)
            data[r][0] = attributes[r];

        for (int c = 0; c < numCountries; c++) {
            Country ctry = selectedCountries.get(c);

            fillRow(data, attributes, c, ctry);
        }

        JTable table = new JTable(data, colNames) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table.setRowHeight(32);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        ((DefaultTableCellRenderer)table.getTableHeader()
                .getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
            table.getColumnModel().getColumn(i).setPreferredWidth(230);
        }

        JScrollPane scroll = new JScrollPane(table);

        JPanel root = new JPanel(new BorderLayout());
        root.add(flagsPanel, BorderLayout.NORTH);
        root.add(scroll, BorderLayout.CENTER);

        JFrame frame = new JFrame("Country Comparison");
        frame.setContentPane(root);
        frame.setSize(1400, 850);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void fillRow(Object[][] data, String[] attributes, int col, Country c) {
        for (int r = 0; r < attributes.length; r++) {
            switch (attributes[r]) {
                case "Name": data[r][col + 1] = c.getName(); break;
                case "Capital": data[r][col + 1] = c.getCapital().orElse("N/A"); break;
                case "Region": data[r][col + 1] = c.getRegion(); break;
                case "Subregion": data[r][col + 1] = c.getSubregion().orElse("N/A"); break;
                case "Population": data[r][col + 1] = c.getPopulation(); break;
                case "Area (km²)":
                    data[r][col + 1] = String.format("%.2f", c.getAreaKm2()); break;
                case "Density (people/km²)":
                    data[r][col + 1] = c.getAreaKm2() > 0
                            ? String.format("%.2f", (double) c.getPopulation() / c.getAreaKm2())
                            : "0.00";
                    break;
                case "Languages": data[r][col + 1] = String.join(", ", c.getLanguages()); break;
                case "Currencies": data[r][col + 1] = String.join(", ", c.getCurrencies()); break;
            }
        }
    }

    private static ImageIcon loadFlag(Country c, int w, int h) {
        try {
            Image img = new ImageIcon(new URL(c.getFlagUrl())).getImage()
                    .getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
