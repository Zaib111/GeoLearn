package app.views.compare;

import app.Navigator;
import app.controllers.CompareController;
import app.entities.Country;
import app.use_cases.compare.CompareViewModel;
import app.views.AbstractView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * View for the Compare Countries feature.
 *
 * Same behavior and UI as the original version, but:
 *  - No direct data access (no APICountryDataAccessObject here)
 *  - All data comes from CompareViewModel / CompareState
 *  - User actions are delegated to CompareController
 */
public class CompareView extends AbstractView {

    private final CompareViewModel viewModel;
    private final CompareController compareController;
    private final Navigator navigator;

    private JComboBox<Integer> countComboBox;
    @SuppressWarnings("unchecked")
    private JComboBox<String>[] dropdowns = new JComboBox[5];
    private JButton compareButton;

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
        countComboBox = new JComboBox<>(countChoices);
        countComboBox.setSelectedItem(2);
        countPanel.add(countLabel);
        countPanel.add(countComboBox);
        add(countPanel);

        JPanel countriesPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        for (int i = 0; i < 5; i++) {
            JLabel label = new JLabel("Country " + (i + 1) + ":");
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.setEnabled(false); // enabled once country list is loaded
            dropdowns[i] = comboBox;
            countriesPanel.add(label);
            countriesPanel.add(comboBox);
        }
        add(countriesPanel);

        countComboBox.addActionListener(e -> updateVisibleDropdowns());

        add(Box.createRigidArea(new Dimension(0, 20)));

        compareButton = new JButton("Compare Countries");
        compareButton.setAlignmentX(CENTER_ALIGNMENT);
        compareButton.setEnabled(false); // enabled once we have countries
        add(compareButton);

        compareButton.addActionListener(e -> {
            int count = (Integer) countComboBox.getSelectedItem();
            List<String> selectedNames = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String name = (String) dropdowns[i].getSelectedItem();
                if (name != null) {
                    selectedNames.add(name);
                }
            }

            // Keep simple duplicate check in view (UX-friendly),
            // but all core validation is also in interactor.
            Set<String> unique = new HashSet<>(selectedNames);
            if (unique.size() < selectedNames.size()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Each country must be unique.",
                        "Duplicate",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedNames.size() < 2) {
                JOptionPane.showMessageDialog(
                        this,
                        "Pick at least two countries.",
                        "Not enough countries",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Delegate actual compare logic to use case
            compareController.compareCountries(selectedNames);
        });
    }

    private void updateVisibleDropdowns() {
        int count = (Integer) countComboBox.getSelectedItem();
        for (int i = 0; i < dropdowns.length; i++) {
            dropdowns[i].setEnabled(i < count && dropdowns[i].getItemCount() > 0);
        }
    }

    // ----------------- AbstractView lifecycle methods -----------------

    @Override
    public void onViewOpened(String param) {
        // When the view opens, ask to load all countries
        compareController.loadAvailableCountries();
    }

    @Override
    public void onViewClosed() {
        // Optional: clear state if needed in future
    }

    @Override
    public void onStateChange(Object oldState, Object newState) {
        if (!(newState instanceof CompareState)) {
            return;
        }
        CompareState state = (CompareState) newState;

        // Show any errors from use case
        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    "Compare Countries Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Populate dropdowns with full country list
        if (state.getCountryNames() != null && !state.getCountryNames().isEmpty()) {
            updateDropdownOptions(state.getCountryNames());
        }

        // When comparison data is ready, show popup window like original code
        if (state.getSelectedCountries() != null
                && !state.getSelectedCountries().isEmpty()
                && state.getColumnHeaders() != null
                && state.getColumnHeaders().length > 0
                && state.getComparisonTableData() != null
                && state.getComparisonTableData().length > 0) {

            showComparisonWindow(state);
        }
    }

    // ----------------- Helpers to update UI from state -----------------

    private void updateDropdownOptions(List<String> countryNames) {
        // Exactly like your old behavior: all countries in every dropdown
        String[] options = countryNames.toArray(new String[0]);

        for (JComboBox<String> comboBox : dropdowns) {
            comboBox.removeAllItems();
            for (String name : options) {
                comboBox.addItem(name);
            }
        }

        compareButton.setEnabled(true);
        updateVisibleDropdowns();
    }

    /**
     * Opens a new window showing flags (aligned above country columns)
     * and a vertical attribute-by-country comparison table,
     * using data from CompareState instead of recomputing.
     */
    private void showComparisonWindow(CompareState state) {
        List<Country> selectedCountries = state.getSelectedCountries();
        String[] colNames = state.getColumnHeaders();
        Object[][] data = state.getComparisonTableData();

        int numCountries = selectedCountries.size();

        JPanel flagsPanel = new JPanel(new GridLayout(1, numCountries + 1, 10, 10));
        flagsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        flagsPanel.add(new JPanel()); // placeholder over "Attribute" column

        for (Country c : selectedCountries) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

            ImageIcon flag = loadFlag(c, 140, 90);
            JLabel img = (flag != null
                    ? new JLabel(flag)
                    : new JLabel("No Flag Found", SwingConstants.CENTER));

            img.setAlignmentX(CENTER_ALIGNMENT);
            if (flag == null) {
                img.setForeground(Color.RED);
                img.setFont(new Font("Dialog", Font.BOLD, 13));
            }
            card.add(img);

            JLabel name = new JLabel(c.getName());
            name.setFont(new Font("Dialog", Font.BOLD, 14));
            name.setAlignmentX(CENTER_ALIGNMENT);
            card.add(Box.createRigidArea(new Dimension(0, 8)));
            card.add(name);

            flagsPanel.add(card);
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
        // Hyperlink implementation
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Point point = e.getPoint();
                    int viewCol = table.columnAtPoint(point);

                    // Check if the click was on a valid row
                    if (viewCol >= 1) {
                        int modelCol = table.convertColumnIndexToModel(viewCol);
                        Country clickedCountry = selectedCountries.get(modelCol-1);
                        String countryCode = clickedCountry.getCode();
                        navigator.navigateTo("country_details", countryCode);
                    }
                }
            }
        });

        // Changes cursor to indicate clickable item to User
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                int viewCol = table.columnAtPoint(point);
                if (viewCol >= 1) {
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        JFrame frame = new JFrame("Country Comparison");
        frame.setContentPane(root);
        frame.setSize(1400, 850);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static ImageIcon loadFlag(Country c, int w, int h) {
        try {
            String url = c.getFlagUrl();
            if (url == null || url.isEmpty()) {
                return null;
            }
            Image img = new ImageIcon(new URL(url)).getImage()
                    .getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
