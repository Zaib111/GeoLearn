package app.views.compare;

import app.NavigationService;
import app.controllers.CompareController;
import app.entities.Country;
import app.use_cases.compare.CompareViewModel;
import app.views.AbstractView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
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
 */
public class CompareView extends AbstractView {

    private final CompareViewModel viewModel;
    private final CompareController compareController;
    private final NavigationService navigationService;

    private JComboBox<Integer> countComboBox;
    @SuppressWarnings("unchecked")
    private JComboBox<String>[] dropdowns = new JComboBox[5];
    private JButton compareButton;

    public CompareView(CompareViewModel viewModel,
                       CompareController compareController,
                       NavigationService navigationService) {
        super(viewModel);
        this.viewModel = viewModel;
        this.compareController = compareController;
        this.navigationService = navigationService;

        buildSelectionUI();
    }

    /**
     * Selection screen UI.
     */
    private void buildSelectionUI() {
        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel heading = new JLabel("Compare Countries");
        heading.setFont(new Font("Dialog", Font.BOLD, 26));
        heading.setAlignmentX(CENTER_ALIGNMENT);
        add(heading);
        add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel countPanel = new JPanel();
        JLabel countLabel = new JLabel("Select the Number of Countries To Compare:");
        Integer[] countChoices = {2, 3, 4, 5};
        countComboBox = new JComboBox<>(countChoices);
        countComboBox.setSelectedItem(2);
        countPanel.add(countLabel);
        countPanel.add(countComboBox);
        add(countPanel);

        JPanel countriesPanel = new JPanel(new GridLayout(5, 2, 6, 6));
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

        add(Box.createRigidArea(new Dimension(0, 10)));

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
            } else {
                compareController.compareCountries(selectedNames);
            }
        });

        revalidate();
        repaint();
    }

    private void updateVisibleDropdowns() {
        int count = (Integer) countComboBox.getSelectedItem();
        for (int i = 0; i < dropdowns.length; i++) {
            dropdowns[i].setEnabled(i < count && dropdowns[i].getItemCount() > 0);
        }
    }

    @Override
    public void onViewOpened(String param) {
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

        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    state.getErrorMessage(),
                    "Compare Countries Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        if (state.getCountryNames() != null && !state.getCountryNames().isEmpty()) {
            updateDropdownOptions(state.getCountryNames());
        }

        if (state.getSelectedCountries() != null
                && !state.getSelectedCountries().isEmpty()
                && state.getColumnHeaders() != null
                && state.getColumnHeaders().length > 0
                && state.getComparisonTableData() != null
                && state.getComparisonTableData().length > 0) {

            showComparisonInSamePanel(state);
        }
    }

    private void updateDropdownOptions(List<String> countryNames) {
        // All countries in every dropdown
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

    private void showComparisonInSamePanel(CompareState state) {
        List<Country> selectedCountries = state.getSelectedCountries();
        String[] colNames = state.getColumnHeaders();
        Object[][] data = state.getComparisonTableData();

        if (colNames != null && colNames.length > 0) {
            for (int i = 0; i < colNames.length; i++) {
                colNames[i] = "";
            }
        }

        removeAll();
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(0, -5, 5, 10));

        JPanel buttonsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton backToSelectionButton = new JButton("Back to Country Selection");
        backToSelectionButton.addActionListener(e -> {
            buildSelectionUI();
            compareController.loadAvailableCountries();
        });
        buttonsRow.add(backToSelectionButton);
        headerPanel.add(buttonsRow);

        JLabel title = new JLabel("Country Comparison");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(title);

        add(headerPanel, BorderLayout.NORTH);

        // ----- Build table data with a top flag row -----
        int rows = data.length;
        int cols = data[0].length;
        Object[][] tableData = new Object[rows + 1][cols];

        // Row 0: flags (first cell now empty)
        tableData[0][0] = "";
        for (int c = 1; c < cols; c++) {
            Country country = selectedCountries.get(c - 1);
            ImageIcon flagIcon = loadFlag(country, 100, 65);
            if (flagIcon != null) {
                tableData[0][c] = flagIcon;
            } else {
                tableData[0][c] = "No Flag Found";
            }
        }

        for (int r = 0; r < rows; r++) {
            System.arraycopy(data[r], 0, tableData[r + 1], 0, cols);
        }

        JTable table = new JTable(tableData, colNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                Object value = getValueAt(0, column);
                return value == null ? Object.class : value.getClass();
            }
        };

        table.setRowHeight(0, 60);
        for (int r = 1; r < table.getRowCount(); r++) {
            table.setRowHeight(r, 24);
        }

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = table.getTableHeader();
        DefaultTableCellRenderer headerRenderer =
                (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Cell renderer that understands ImageIcon
        DefaultTableCellRenderer center = new IconAwareCenterRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
            table.getColumnModel().getColumn(i).setPreferredWidth(170);
        }

        JScrollPane scroll = new JScrollPane(
                table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scroll.setPreferredSize(new Dimension(1400, 400));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    Point point = e.getPoint();
                    int viewCol = table.columnAtPoint(point);

                    if (viewCol >= 1) {
                        int modelCol = table.convertColumnIndexToModel(viewCol);
                        if (modelCol - 1 >= 0 && modelCol - 1 < selectedCountries.size()) {
                            Country clickedCountry = selectedCountries.get(modelCol - 1);
                            navigationService.navigateTo("country_details", clickedCountry.getCode());
                        }
                    }
                }
            }
        });

        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                int viewRow = table.rowAtPoint(point);

                if (viewRow >= 0) {
                    // Displays clickable cursor
                    table.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        add(scroll, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    /**
     * Renderer that centers text and properly displays ImageIcons.
     */
    private static class IconAwareCenterRenderer extends DefaultTableCellRenderer {
        IconAwareCenterRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        protected void setValue(Object value) {
            if (value instanceof Icon) {
                setIcon((Icon) value);
                setText("");
            } else {
                setIcon(null);
                super.setValue(value);
            }
        }
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
