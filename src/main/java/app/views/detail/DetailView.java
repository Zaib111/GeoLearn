package app.views.detail;

import app.NavigationService;
import app.controllers.DetailController;
import app.use_cases.detail.DetailInputData;
import app.views.AbstractView;
import app.views.ViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Class for generating the view of the country details page, in line with Clean Architecture
 * Displays the detailed info of a country accessed from one of the other program modes.
 * Allows traversal to the details page of neighbouring countries as well.
 */
public class DetailView extends AbstractView {
    private final DetailController controller;

    // The container that holds the different views (details vs error)
    private final JPanel contentPanel = new JPanel(new CardLayout());
    // The label used to display error messages when necessary
    private final JLabel errorLabel = new JLabel();

    // Constants to identify the different cards in the CardLayout
    private static final String VIEW_DETAILS = "details";
    private static final String VIEW_ERROR = "error";

    // Info labels for displaying country specific data
    private final JLabel titleLabel = new JLabel();
    private final JLabel flagLabel = new JLabel();
    private final JLabel codeLabel = new JLabel();
    private final JLabel capitalLabel = new JLabel();
    private final JLabel regionLabel = new JLabel();
    private final JLabel populationLabel = new JLabel();
    private final JLabel areaLabel = new JLabel();
    private final JTextArea bordersArea = new JTextArea(3, 20);
    private final JTextArea languagesArea = new JTextArea(3, 20);
    private final JTextArea currenciesArea = new JTextArea(3, 20);
    private final JTextArea timezonesArea = new JTextArea(3, 20);

    private final NavigationService navigator;

    /**
     * Constructor for this class.
     * Passes the required view model, controller, and navigator to perform the view's functions
     * Creates the overall layout for the view.
     *
     * @param detailViewModel The ViewModel containing the state for this view.
     * @param controller      The controller to handle user actions and data loading.
     * @param navigator       The navigator to handle screen switching.
     */
    public DetailView(ViewModel<DetailState> detailViewModel, DetailController controller, NavigationService navigator) {
        // Initialize the parent AbstractView with the ViewModel
        super(detailViewModel);
        this.controller = controller;
        this.navigator = navigator;

        // Set the main layout for the panel with specific gaps
        setLayout(new BorderLayout(15, 15));
        // Add padding around the edges of the view
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize the header panel to hold the country title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        headerPanel.add(titleLabel);

        // Initialize the flag panel to hold the country flag image
        JPanel flagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        flagPanel.add(flagLabel);
        flagLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Create the main container for the list of details, using a vertical Box layout
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add standard single-line detail panels
        addDetailPanel(detailsPanel, "Code:", codeLabel);
        addDetailPanel(detailsPanel, "Capital:", capitalLabel);
        addDetailPanel(detailsPanel, "Region:", regionLabel);
        addDetailPanel(detailsPanel, "Population:", populationLabel);
        addDetailPanel(detailsPanel, "Area (km²):", areaLabel);

        // Add multi-line text area panels for lists (borders, languages, etc.)
        addBorderTextAreaPanel(detailsPanel, "Borders:", bordersArea);
        addTextAreaPanel(detailsPanel, "Languages:", languagesArea);
        addTextAreaPanel(detailsPanel, "Currencies:", currenciesArea);
        addTextAreaPanel(detailsPanel, "Timezones:", timezonesArea);

        // Combine the header (title) and flag into a top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.WEST);
        topPanel.add(flagPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Wrap the details panel in a scroll pane to handle overflow
        JScrollPane scrollDetails = new JScrollPane(detailsPanel);

        // Add the scrollable details to the center of the layout
        add(scrollDetails, BorderLayout.CENTER);

        // Configure the visual properties of the Error Label
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        errorLabel.setForeground(Color.RED);

        // Add both the details view and the error view to the CardLayout container
        contentPanel.add(scrollDetails, VIEW_DETAILS);
        contentPanel.add(errorLabel, VIEW_ERROR);

        // Add the CardLayout panel to the center of the main view
        add(contentPanel, BorderLayout.CENTER);

        // Refresh the UI to ensure everything is rendered correctly
        this.revalidate();
        this.repaint();
    }

    /**
     * Triggered when the view is opened by the navigator.
     * Creates the input data object and instructs the controller to load the details.
     *
     * @param param The parameter passed during navigation (e.g., country name or code).
     */
    @Override
    public void onViewOpened(String param) {
        // Create the input data object with the country parameter
        DetailInputData inputData = new DetailInputData(param);
        // Instruct the controller to fetch the data
        controller.loadDetails(inputData);
        this.revalidate();
        this.repaint();
    }

    /**
     * Triggered when the view is closed or navigated away from.
     * Used for cleanup if necessary.
     */
    @Override
    public void onViewClosed() {}

    /**
     * Responds to property changes in the ViewModel state.
     * Updates the UI components to reflect the current state (Error or Success).
     *
     * @param oldState The previous state object.
     * @param newState The new state object containing updated data.
     */
    @Override
    public void onStateChange(Object oldState, Object newState) {
        DetailState detailState = (DetailState) newState;
        // Retrieve the CardLayout manager from the content panel
        CardLayout cl = (CardLayout) (contentPanel.getLayout());

        if (detailState.getHasError()) {
            // Update the error label with the message from the state
            errorLabel.setText(detailState.getErrorMessage());

            // Instruct the CardLayout to flip to the Error view
            cl.show(contentPanel, VIEW_ERROR);
        } else {
            // Populate the standard labels
            titleLabel.setText(detailState.getCountryName());
            codeLabel.setText(detailState.getCountryCode());
            // Attempt to load and display the flag image
            displayFlag(detailState.getFlagUrl());

            // Handle optional capital city data
            String capital = detailState.getCapital().orElse("N/A");
            capitalLabel.setText(capital);

            // Format and display complex strings like region and subregion
            regionLabel.setText(detailState.getRegion() + detailState.getSubregion().map(s -> " (" + s + ")").orElse(""));
            populationLabel.setText(String.format("%,d", detailState.getPopulation()));
            areaLabel.setText(String.format("%,.2f", detailState.getAreaKm2()));

            // Join list data into strings and populate text areas
            bordersArea.setText(joinList(detailState.getBorders()));
            languagesArea.setText(joinList(detailState.getLanguages()));
            currenciesArea.setText(joinList(detailState.getCurrencies()));
            timezonesArea.setText(joinList(detailState.getTimezones()));

            // Instruct the CardLayout to flip back to the Details view
            cl.show(contentPanel, VIEW_DETAILS);
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Helper function for adding a standard label-based detail row to the panel.
     *
     * @param targetPanel The parent panel to add this row to.
     * @param title       The title string (e.g., "Capital:").
     * @param valueLabel  The JLabel component that will hold the dynamic value.
     */
    private void addDetailPanel(JPanel targetPanel, String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Calculate the preferred size but modify width to max integer
        Dimension d = panel.getPreferredSize();
        d.width = Integer.MAX_VALUE;
        // Force the panel to extend to the full width of the container to prevent alignment issues in BoxLayout
        panel.setMaximumSize(d);

        targetPanel.add(panel);
    }

    /**
     * Helper function for adding a scrollable text area to the panel.
     * Used for lists of data like languages or currencies.
     *
     * @param targetPanel The parent panel to add this row to.
     * @param title       The title string.
     * @param textArea    The JTextArea component that will hold the data.
     */
    private void addTextAreaPanel(JPanel targetPanel, String title, JTextArea textArea) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Configure text area to be read-only and wrap words
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Wrap text area in a scroll pane with a fixed preferred height
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 60));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(scrollPane);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Force the panel to extend to the full width of the container
        Dimension d = panel.getPreferredSize();
        d.width = Integer.MAX_VALUE;
        panel.setMaximumSize(d);

        targetPanel.add(panel);
        // Add a rigid area to create vertical spacing between items
        targetPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    /**
     * Specific helper function for the Borders text area.
     * Adds smart cursor and click listeners to allow navigation via border codes.
     *
     * @param targetPanel The parent panel to add this row to.
     * @param title       The title string.
     * @param textArea    The JTextArea component for borders.
     */
    private void addBorderTextAreaPanel(JPanel targetPanel, String title, JTextArea textArea) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(300, 60));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add special listeners for interactive border codes
        addSmartCursor(textArea);
        addSmartClickListener(textArea);

        panel.add(titleLabel);
        panel.add(scrollPane);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Force the panel to extend to the full width of the container
        Dimension d = panel.getPreferredSize();
        d.width = Integer.MAX_VALUE;
        panel.setMaximumSize(d);

        targetPanel.add(panel);
        targetPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    /**
     * Utility method to join a list of strings into a comma-separated string.
     *
     * @param list The list of strings to join.
     * @return A single comma-separated string, or "N/A" if the list is null.
     */
    private String joinList(List<String> list) {
        return list != null ? String.join(", ", list) : "N/A";
    }

    /**
     * Loads the flag image from the provided URL, scales it, and sets it to the flag label.
     * Handles IO exceptions if the image cannot be loaded.
     *
     * @param flagUrl The string URL of the flag image.
     */
    private void displayFlag(String flagUrl) {
        if (flagUrl != null && !flagUrl.isEmpty()) {
            try {
                URL url = new URL(flagUrl);
                ImageIcon icon = new ImageIcon(url);

                // Scale the image down if it's too large, preserving aspect ratio
                int width = 100;
                int height = (icon.getIconHeight() * width) / icon.getIconWidth();
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                flagLabel.setIcon(new ImageIcon(scaledImage));

            } catch (IOException e) {
                // Handle image loading failure gracefully
                flagLabel.setText("Flag not available");
                flagLabel.setIcon(null);
                System.err.println("Failed to load flag image: " + e.getMessage());
            } catch (Exception e) {
                // Catch any other unexpected exceptions
                flagLabel.setText("Flag not available");
                flagLabel.setIcon(null);
            }
        } else {
            // Handle case where flag URL is null or empty
            flagLabel.setText("Flag N/A");
            flagLabel.setIcon(null);
        }
    }

    /**
     * Adds a mouse listener to the text area to detect clicks on specific words (country codes).
     * Calculates the position of the click to determine which item in the comma-separated list was clicked,
     * then triggers navigation to that country.
     *
     * @param textArea The JTextArea to attach the listener to.
     */
    private void addSmartClickListener(JTextArea textArea) {
        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    // Get the character offset position in the text based on the mouse coordinates
                    int offset = textArea.viewToModel2D(e.getPoint());
                    if (offset >= textArea.getDocument().getLength()) {
                        return;
                    }
                    String text = textArea.getText();

                    // Identify the boundaries of the clicked word by searching for commas
                    int start = text.lastIndexOf(", ", offset);
                    int end = text.indexOf(", ", offset);

                    // Adjust start index to account for missing comma (start of text)
                    if (start == -1) {
                        start = 0;
                    } else {
                        // Skip the comma and the following space
                        start += 2;
                    }

                    // Adjust end index if no further commas are found (end of text)
                    if (end == -1) {
                        end = text.length();
                    }

                    // Validate that the click occurred strictly within the calculated word boundaries
                    if (offset >= start && offset <= end) {
                        String clickedValue = text.substring(start, end).trim();

                        // Perform navigation if the value is valid
                        if (!clickedValue.isEmpty() && !clickedValue.equals(",")) {
                            navigator.navigateTo("country_details", clickedValue);
                        }
                    }
                }
            }
        });
    }

    /**
     * Adds a mouse motion listener to change the cursor to a hand icon when hovering over
     * a clickable item in the text area.
     *
     * @param textArea The JTextArea to attach the listener to.
     */
    private void addSmartCursor(JTextArea textArea) {
        textArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                try {
                    boolean isHoveringValid = false;

                    // Calculate the text offset from the mouse position
                    int offset = textArea.viewToModel2D(e.getPoint());

                    if (offset >= 0 && offset < textArea.getDocument().getLength()) {
                        String text = textArea.getText();

                        // Scan backwards for the start of the word (comma delimiter)
                        int start = text.lastIndexOf(", ", offset);
                        // Scan forwards for the end of the word
                        int end = text.indexOf(", ", offset);

                        // Adjust start position
                        if (start == -1) {
                            start = 0;
                        } else {
                            // Skip the comma and space
                            start += 2;
                        }

                        // Adjust end position
                        if (end == -1) {
                            end = text.length();
                        }

                        // Check if the cursor is hovering over the actual text of the word
                        if (offset >= start && offset <= end) {
                            String hoveredText = text.substring(start, end).trim();
                            if (!hoveredText.isEmpty() && !hoveredText.equals(",")) {
                                isHoveringValid = true;
                            }
                        }
                    }

                    // Change cursor to HAND if hovering over valid text, else DEFAULT
                    if (isHoveringValid) {
                        textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else {
                        textArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }

                } catch (Exception ex) {
                    textArea.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });
    }
}
