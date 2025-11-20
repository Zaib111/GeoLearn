package view;

import data_access.APICountryDataAccessObject;
import entity.Country;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.List;

public class CompareView {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            APICountryDataAccessObject dao = new APICountryDataAccessObject();
            List<Country> countries = dao.getCountries();

            if (countries == null || countries.isEmpty()) {
                JOptionPane.showMessageDialog(
                        null,
                        "Could not load countries from API.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Map: display name -> Country
            Map<String, Country> countryByName = new HashMap<String, Country>();
            for (Country c : countries) {
                String rawName = String.valueOf(c.getName());
                String name = stripOptionalAndBrackets(rawName);
                if (!name.isEmpty()) {
                    countryByName.put(name, c);
                }
            }

            String[] countryOptions = countryByName.keySet().toArray(new String[0]);
            Arrays.sort(countryOptions);

            // --------- Main UI panel ---------
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));

            // Heading
            JLabel heading = new JLabel("Compare Countries");
            heading.setFont(new Font("Dialog", Font.BOLD, 26));
            heading.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(heading);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Number of countries section
            JPanel countPanel = new JPanel();
            JLabel countLabel = new JLabel("Number of countries to compare:");
            Integer[] countChoices = {2, 3, 4, 5};
            JComboBox<Integer> countComboBox = new JComboBox<Integer>(countChoices);
            countComboBox.setSelectedItem(2);
            countPanel.add(countLabel);
            countPanel.add(countComboBox);
            panel.add(countPanel);

            // Country dropdowns
            JPanel countriesPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            @SuppressWarnings("unchecked")
            JComboBox<String>[] countryComboBoxes = new JComboBox[5];

            for (int i = 0; i < 5; i++) {
                JLabel label = new JLabel("Country " + (i + 1) + ":");
                JComboBox<String> comboBox = new JComboBox<String>(countryOptions);
                countryComboBoxes[i] = comboBox;
                countriesPanel.add(label);
                countriesPanel.add(comboBox);
            }
            panel.add(countriesPanel);

            // Enable only first N dropdowns
            ActionListener updateEnabledDropdowns = e -> {
                int count = (Integer) countComboBox.getSelectedItem();
                for (int i = 0; i < countryComboBoxes.length; i++) {
                    countryComboBoxes[i].setEnabled(i < count);
                }
            };
            updateEnabledDropdowns.actionPerformed(null);
            countComboBox.addActionListener(updateEnabledDropdowns);

            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Compare button
            JButton compareButton = new JButton("Compare Countries");
            compareButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(compareButton);

            // Frame setup
            JFrame frame = new JFrame("Compare Countries View");
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1400, 850);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Button logic
            compareButton.addActionListener(e -> {
                int count = (Integer) countComboBox.getSelectedItem();
                List<Country> selectedCountries = new ArrayList<Country>();
                List<String> selectedNames = new ArrayList<String>();

                for (int i = 0; i < count; i++) {
                    Object value = countryComboBoxes[i].getSelectedItem();
                    if (value != null) {
                        String name = value.toString();
                        selectedNames.add(name);
                        Country c = countryByName.get(name);
                        if (c != null) {
                            selectedCountries.add(c);
                        }
                    }
                }

                // Prevent duplicates
                Set<String> unique = new HashSet<String>(selectedNames);
                if (unique.size() < selectedNames.size()) {
                    JOptionPane.showMessageDialog(
                            panel,
                            "Each selected country must be unique. Please choose different countries.",
                            "Duplicate selection",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                if (selectedCountries.size() < 2) {
                    JOptionPane.showMessageDialog(
                            panel,
                            "Please select at least two countries.",
                            "Invalid selection",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                showComparisonWindow(selectedCountries);
            });
        });
    }

    /**
     * Opens a new window showing flags (aligned above country columns)
     * and a vertical attribute-by-country comparison table.
     */
    private static void showComparisonWindow(List<Country> selectedCountries) {
        int numCountries = selectedCountries.size();

        // ---- Top: flags row with a blank "Attribute" slot on the left ----
        JPanel flagsPanel = new JPanel(new GridLayout(1, numCountries + 1, 10, 10));
        flagsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // First cell = placeholder above the "Attribute" column
        JPanel placeholder = new JPanel();
        placeholder.setOpaque(false);
        flagsPanel.add(placeholder);

        // One card per country, centered above its table column
        for (Country c : selectedCountries) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setAlignmentX(Component.CENTER_ALIGNMENT);

            // Slightly smaller flags so 4–5 fit nicely
            ImageIcon flagIcon = loadFlagIcon(c, 140, 90);
            JLabel flagLabel;
            if (flagIcon != null) {
                flagLabel = new JLabel(flagIcon);
            } else {
                flagLabel = new JLabel("No Flag Found", SwingConstants.CENTER);
                flagLabel.setForeground(Color.RED);
                flagLabel.setFont(new Font("Dialog", Font.BOLD, 13));
            }
            flagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            String name = stripOptionalAndBrackets(String.valueOf(c.getName()));
            JLabel nameLabel = new JLabel(name);
            nameLabel.setFont(new Font("Dialog", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(flagLabel);
            card.add(Box.createRigidArea(new Dimension(0, 8)));
            card.add(nameLabel);
            flagsPanel.add(card);
        }

        // ---- Bottom: vertical table of attributes vs countries ----
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
        int cols = numCountries + 1; // first column is "Attribute"

        String[] columnNames = new String[cols];
        columnNames[0] = "Attribute";
        for (int i = 0; i < numCountries; i++) {
            columnNames[i + 1] = stripOptionalAndBrackets(String.valueOf(selectedCountries.get(i).getName()));
        }

        Object[][] data = new Object[rows][cols];

        for (int r = 0; r < rows; r++) {
            data[r][0] = attributes[r];

            for (int cIdx = 0; cIdx < numCountries; cIdx++) {
                Country country = selectedCountries.get(cIdx);
                Object value;

                String attr = attributes[r];
                if ("Name".equals(attr)) {
                    value = stripOptionalAndBrackets(String.valueOf(country.getName()));
                } else if ("Capital".equals(attr)) {
                    value = stripOptionalAndBrackets(String.valueOf(country.getCapital()));
                } else if ("Region".equals(attr)) {
                    value = stripOptionalAndBrackets(String.valueOf(country.getRegion()));
                } else if ("Subregion".equals(attr)) {
                    value = stripOptionalAndBrackets(String.valueOf(country.getSubregion()));
                } else if ("Population".equals(attr)) {
                    value = stripOptionalAndBrackets(String.valueOf(country.getPopulation()));
                } else if ("Area (km²)".equals(attr)) {
                    double area = safeGetArea(country);
                    value = String.format("%.2f", area);
                } else if ("Density (people/km²)".equals(attr)) {
                    double area = safeGetArea(country);
                    String popStr = stripOptionalAndBrackets(String.valueOf(country.getPopulation()));
                    long pop = 0L;
                    try {
                        pop = Long.parseLong(popStr);
                    } catch (NumberFormatException ignored) {
                    }
                    double density = area > 0 ? (double) pop / area : 0.0;
                    value = String.format("%.2f", density);
                } else if ("Languages".equals(attr)) {
                    value = listOrObjectToString(country.getLanguages());
                } else if ("Currencies".equals(attr)) {
                    value = listOrObjectToString(country.getCurrencies());
                } else {
                    value = "";
                }

                data[r][cIdx + 1] = value;
            }
        }

        JTable table = new JTable(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };

        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Center align data and headers; set wider columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int col = 0; col < table.getColumnModel().getColumnCount(); col++) {
            table.getColumnModel().getColumn(col).setPreferredWidth(230);
            table.getColumnModel().getColumn(col).setCellRenderer(centerRenderer);
        }
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(SwingConstants.CENTER);

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setCellSelectionEnabled(false);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // ---- Put flags on top and table below ----
        JPanel root = new JPanel(new BorderLayout());
        root.add(flagsPanel, BorderLayout.NORTH);
        root.add(tableScroll, BorderLayout.CENTER);

        JFrame compareFrame = new JFrame("Country Comparison");
        compareFrame.setContentPane(root);
        compareFrame.setSize(1400, 850);
        compareFrame.setLocationRelativeTo(null);
        compareFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        compareFrame.setVisible(true);
    }

    /**
     * Load and scale a flag icon.
     */
    private static ImageIcon loadFlagIcon(Country c, int width, int height) {
        String urlStr = safeGetFlagUrl(c);
        if (urlStr == null || urlStr.isEmpty()) {
            return null;
        }
        try {
            ImageIcon original = new ImageIcon(new URL(urlStr));
            Image img = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Try a few possible flag getter names via reflection.
     */
    private static String safeGetFlagUrl(Country c) {
        String[] methods = {"getFlagUrl", "getFlag", "getFlagPngUrl"};
        for (int i = 0; i < methods.length; i++) {
            String m = methods[i];
            try {
                Method method = c.getClass().getMethod(m);
                Object result = method.invoke(c);
                if (result != null) {
                    return result.toString();
                }
            } catch (Exception ignore) {
                // try next
            }
        }
        return null;
    }

    /**
     * Try to get area using several possible method names; if not available, returns 0.0.
     */
    private static double safeGetArea(Country c) {
        String[] methods = {"getArea", "area", "getAreaInKm2", "getAreaKm2"};
        for (int i = 0; i < methods.length; i++) {
            String m = methods[i];
            try {
                Method method = c.getClass().getMethod(m);
                Object result = method.invoke(c);
                if (result instanceof Number) {
                    return ((Number) result).doubleValue();
                }
                String s = stripOptionalAndBrackets(String.valueOf(result));
                if (!s.isEmpty()) {
                    try {
                        return Double.parseDouble(s);
                    } catch (NumberFormatException ignored) {
                    }
                }
            } catch (Exception ignore) {
                // try next
            }
        }
        return 0.0;
    }

    /**
     * Converts either a List or another object (like "[English]") into a clean CSV string.
     */
    private static String listOrObjectToString(Object value) {
        if (value instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            List<String> cleaned = new ArrayList<String>();
            for (Object o : list) {
                cleaned.add(stripOptionalAndBrackets(String.valueOf(o)));
            }
            return String.join(", ", cleaned);
        }
        return stripOptionalAndBrackets(String.valueOf(value));
    }

    /**
     * Turns "Optional[Ottawa]" -> "Ottawa", "[English]" -> "English", etc.
     */
    private static String stripOptionalAndBrackets(String s) {
        if (s == null) {
            return "";
        }
        s = s.trim();
        if (s.startsWith("Optional[") && s.endsWith("]")) {
            s = s.substring("Optional[".length(), s.length() - 1).trim();
        }
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length() - 1).trim();
        }
        return s;
    }
}
