package app.views.country_collection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.jetbrains.annotations.NotNull;

import app.NavigationService;
import app.controllers.CollectionController;
import app.entities.Country;
import app.entities.CountryCollection;
import app.views.AbstractView;
import app.views.ViewModel;

public class CollectionView extends AbstractView {
    private CollectionController collectionController;
    private JTextField collectionNameField;
    private JTextField countriesField;
    private JButton createButton;
    private JPanel inputPanel;
    private JPanel collectionsPanel;
    private JScrollPane collectionsScrollPane;
    private String font;

    private final NavigationService navigator;

    public CollectionView(ViewModel<CollectionState> collectionViewModel,
                          CollectionController collectionController,
                          NavigationService navigator) {
        super(collectionViewModel);

        this.collectionController = collectionController;
        this.navigator = navigator;
        this.font = "SansSerif";
        configureRootPanel();

        // Create main content panel
        final JPanel contentPanel = createEmptyPanel();

        // Title
        final JLabel titleLabel = createTitleLabel();
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Input panel for creating new collection
        createInputPanel();

        // Collection name input
        final JPanel namePanel = createNameInput();
        inputPanel.add(namePanel);

        // Countries input (comma-separated)
        final JPanel countriesInputPanel = createCountriesInput();
        inputPanel.add(countriesInputPanel);

        // Help text
        final JPanel helpPanel = createHelpText();
        inputPanel.add(helpPanel);

        // Create button
        final JPanel buttonPanel = createButtonPanel();
        inputPanel.add(buttonPanel);

        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Collections display panel
        final JLabel collectionsTitle = createCollectionsTitle();
        contentPanel.add(collectionsTitle);
        contentPanel.add(Box.createVerticalStrut(10));

        createCollectionsPanel();

        createCollectionsScrollPane();

        final JPanel scrollPaneWrapper = createScrollPaneWrapper();
        contentPanel.add(scrollPaneWrapper);

        add(contentPanel, BorderLayout.CENTER);

        // Add action listener for create button
        createButton.addActionListener(event -> handleCreateCollection());
    }

    private void configureRootPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
    }

    @NotNull
    private JPanel createScrollPaneWrapper() {
        final JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
        scrollPaneWrapper.setBackground(Color.WHITE);
        scrollPaneWrapper.add(collectionsScrollPane, BorderLayout.CENTER);
        return scrollPaneWrapper;
    }

    @NotNull
    private JLabel createCollectionsTitle() {
        final JLabel collectionsTitle = new JLabel("Your Collections");
        collectionsTitle.setFont(new Font(font, Font.BOLD, 18));
        collectionsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        return collectionsTitle;
    }

    @NotNull
    private static JPanel createEmptyPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        return panel;
    }

    @NotNull
    private JLabel createTitleLabel() {
        final JLabel titleLabel = new JLabel("Collections");
        titleLabel.setFont(new Font(font, Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titleLabel;
    }

    private void createCollectionsScrollPane() {
        collectionsScrollPane = new JScrollPane(collectionsPanel);
        collectionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        collectionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        collectionsScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        collectionsScrollPane.setPreferredSize(new Dimension(600, 300));
    }

    private void createCollectionsPanel() {
        collectionsPanel = new JPanel();
        collectionsPanel.setLayout(new BoxLayout(collectionsPanel, BoxLayout.Y_AXIS));
        collectionsPanel.setBackground(Color.WHITE);
    }

    @NotNull
    private JPanel createButtonPanel() {
        createButton = new JButton("Create Collection");
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createButton);
        return buttonPanel;
    }

    @NotNull
    private JPanel createHelpText() {
        final JLabel helpLabel = new JLabel("Example: Canada, United States, Mexico");
        helpLabel.setFont(new Font(font, Font.ITALIC, 11));
        helpLabel.setForeground(Color.GRAY);
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        helpPanel.setBackground(Color.WHITE);
        helpPanel.add(helpLabel);
        return helpPanel;
    }

    @NotNull
    private JPanel createCountriesInput() {
        final JPanel countriesInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countriesInputPanel.setBackground(Color.WHITE);
        final JLabel countriesLabel = new JLabel("Countries (comma-separated):");
        countriesField = new JTextField(30);
        countriesInputPanel.add(countriesLabel);
        countriesInputPanel.add(countriesField);
        return countriesInputPanel;
    }

    @NotNull
    private JPanel createNameInput() {
        final JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(Color.WHITE);
        final JLabel nameLabel = new JLabel("Collection Name:");
        collectionNameField = new JTextField(20);
        namePanel.add(nameLabel);
        namePanel.add(collectionNameField);
        return namePanel;
    }

    private void createInputPanel() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Create New Collection",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));
    }

    private void handleCreateCollection() {
        final String collectionName = collectionNameField.getText().trim();
        final String countriesInput = countriesField.getText().trim();
        final String errorTitleText = "Error";

        if (collectionName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a collection name.",
                    errorTitleText,
                    JOptionPane.ERROR_MESSAGE
            );
        }
        else if (countriesInput.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter at least one country name.",
                    errorTitleText,
                    JOptionPane.ERROR_MESSAGE
            );
        }
        else {
            // Parse countries from comma-separated input
            final List<String> countryNames = new ArrayList<>();
            final String[] countryNamesArray = countriesInput.split(",");
            for (String countryName : countryNamesArray) {
                final String trimmedName = countryName.trim();
                if (!trimmedName.isEmpty()) {
                    countryNames.add(trimmedName);
                }
            }

            if (countryNames.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter at least one valid country name.",
                        errorTitleText,
                        JOptionPane.ERROR_MESSAGE
                );
            }
            else {
                collectionController.addCollection(collectionName, countryNames);
                // Clear input fields on successful creation (will be done via state update)
                collectionNameField.setText("");
                countriesField.setText("");
            }
        }
    }

    @Override
    public void onViewOpened(String param) {
        // Fetch all collections when view opens
        collectionController.fetchAllCollections();
    }

    @Override
    public void onViewClosed() {
        // Clear fields when view closes
        collectionNameField.setText("");
        countriesField.setText("");
    }

    @Override
    public void onStateChange(Object oldState, Object newState) {
        final CollectionState state = (CollectionState) newState;

        // Update collections display
        updateCollectionsDisplay(state.getAllCollections());
    }
    
    private void updateCollectionsDisplay(List<CountryCollection> collections) {
        collectionsPanel.removeAll();

        if (collections == null || collections.isEmpty()) {
            final JLabel emptyLabel = new JLabel("No collections yet. Create one above!");
            emptyLabel.setFont(new Font(font, Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            collectionsPanel.add(emptyLabel);
        }
        else {
            for (CountryCollection collection : collections) {
                final JPanel collectionPanel = createCollectionPanel(collection);
                collectionsPanel.add(collectionPanel);
                collectionsPanel.add(Box.createVerticalStrut(10));
            }
        }

        collectionsPanel.revalidate();
        collectionsPanel.repaint();
    }

    private JPanel createCollectionPanel(CountryCollection collection) {
        final JPanel panel = createEmptyPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Collection name with rename button
        final JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(Color.WHITE);
        final JLabel nameLabel = new JLabel(collection.getCollectionName());
        nameLabel.setFont(new Font(font, Font.BOLD, 16));
        namePanel.add(nameLabel);

        final JButton renameButton = new JButton("Rename");
        renameButton.setFont(new Font(font, Font.PLAIN, 11));
        renameButton.addActionListener(event -> handleRenameCollection(collection));
        namePanel.add(renameButton);

        panel.add(namePanel);
        panel.add(Box.createVerticalStrut(5));

        createCollectionPanelDisplayCountries(collection, panel);

        // Action buttons panel
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        final JButton editButton = new JButton("Edit");
        editButton.setFont(new Font(font, Font.PLAIN, 11));
        editButton.addActionListener(event -> handleEditCollection(collection));
        buttonPanel.add(editButton);

        final JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font(font, Font.PLAIN, 11));
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(event -> handleDeleteCollection(collection));
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel);

        return panel;
    }

    private void createCollectionPanelDisplayCountries(CountryCollection collection, JPanel panel) {
        // Display countries with flags and stats
        final List<Country> countries = collection.getCountries();
        if (countries != null && !countries.isEmpty()) {
            // Create a panel to hold country cards
            final JPanel countriesPanel = createEmptyPanel();

            for (Country country : countries) {
                final JPanel countryCard = createCountryCard(country);
                countriesPanel.add(countryCard);
                countriesPanel.add(Box.createVerticalStrut(5));
            }

            final JScrollPane countriesScroll = new JScrollPane(countriesPanel);
            countriesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            countriesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            countriesScroll.setPreferredSize(new Dimension(450, Math.min(200, countries.size() * 80)));
            countriesScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            countriesScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(countriesScroll);
        }
        else {
            final JLabel emptyLabel = new JLabel("No countries in this collection");
            emptyLabel.setFont(new Font(font, Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(emptyLabel);
        }

        panel.add(Box.createVerticalStrut(5));
    }

    private void handleRenameCollection(CountryCollection collection) {
        final String newName = JOptionPane.showInputDialog(
                this,
                "Enter new collection name:",
                "Rename Collection",
                JOptionPane.QUESTION_MESSAGE
        );

        if (newName != null && !newName.trim().isEmpty()) {
            collectionController.renameCollection(collection.getCollectionId(), newName.trim());
        }
    }

    private void handleEditCollection(CountryCollection collection) {
        // Create a dialog for editing collection
        final JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Collection", true);
        editDialog.setLayout(new BorderLayout(10, 10));
        editDialog.setSize(500, 400);

        // Center the dialog
        editDialog.setLocationRelativeTo(this);

        final JPanel currentPanel = handleEditCollectionCurrentPanel(collection);

        // Input panel
        final JPanel editCollectionInputPanel = new JPanel();
        editCollectionInputPanel.setLayout(new BoxLayout(editCollectionInputPanel, BoxLayout.Y_AXIS));
        editCollectionInputPanel.setBorder(BorderFactory.createTitledBorder("Add/Remove Countries"));

        final JLabel addLabel = new JLabel("Countries to add (comma-separated):");
        final JTextField addField = new JTextField(30);

        final JLabel removeLabel = new JLabel("Countries to remove (comma-separated):");
        final JTextField removeField = new JTextField(30);

        editCollectionInputPanel.add(addLabel);
        editCollectionInputPanel.add(addField);
        editCollectionInputPanel.add(Box.createVerticalStrut(10));
        editCollectionInputPanel.add(removeLabel);
        editCollectionInputPanel.add(removeField);

        // Button panel
        final JPanel buttonPanel = new JPanel(new FlowLayout());
        final JButton saveButton = new JButton("Save");
        final JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final List<String> toAdd = CollectionView.this.parseCountryNames(addField.getText());
                final List<String> toRemove = CollectionView.this.parseCountryNames(removeField.getText());

                if (!toAdd.isEmpty() || !toRemove.isEmpty()) {
                    collectionController.editCollection(collection.getCollectionId(), toAdd, toRemove);
                    editDialog.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(
                            editDialog,
                            "Please enter countries to add or remove.",
                            "No Changes",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });

        cancelButton.addActionListener(event -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editDialog.add(currentPanel, BorderLayout.NORTH);
        editDialog.add(editCollectionInputPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);

        editDialog.setVisible(true);
    }

    @NotNull
    private JPanel handleEditCollectionCurrentPanel(CountryCollection collection) {
        // Current countries display
        final JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.setBorder(BorderFactory.createTitledBorder("Current Countries"));
        final JTextArea currentCountries = new JTextArea();
        currentCountries.setEditable(false);
        currentCountries.setFont(new Font(font, Font.PLAIN, 12));

        final List<Country> countries = collection.getCountries();
        if (countries != null && !countries.isEmpty()) {
            final String countriesText = countries.stream()
                    .map(Country::getName)
                    .collect(Collectors.joining("\n"));
            currentCountries.setText(countriesText);
        }

        final JScrollPane currentScroll = new JScrollPane(currentCountries);
        currentScroll.setPreferredSize(new Dimension(400, 150));
        currentPanel.add(currentScroll, BorderLayout.CENTER);
        return currentPanel;
    }

    private void handleDeleteCollection(CountryCollection collection) {
        final int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the collection \"" + collection.getCollectionName() + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            collectionController.deleteCollection(collection.getCollectionId());
        }
    }

    private List<String> parseCountryNames(String input) {
        final List<String> countryNames = new ArrayList<>();
        if (input != null && !input.trim().isEmpty()) {
            final String[] names = input.split(",");
            for (String name : names) {
                final String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    countryNames.add(trimmed);
                }
            }
        }
        return countryNames;
    }

    private JPanel createCountryCard(Country country) {
        final JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));
        card.setBackground(Color.WHITE);

        final JPanel flagPanel = createCountryCardFlagPanel(country);
        final JPanel infoPanel = createCountryCardInfoPanel(country);

        card.add(flagPanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);

        // Hyperlink implementation
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    final String countryCode = country.getCode();
                    navigator.navigateTo("country_details", countryCode);

                }
            }
        });

        // Changes cursor to indicate clickable item to User
        card.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        return card;
    }

    @NotNull
    private JPanel createCountryCardInfoPanel(Country country) {
        // Country info panel
        final JPanel infoPanel = createEmptyPanel();

        // Country name
        final JLabel nameLabel = new JLabel(country.getName());
        nameLabel.setFont(new Font(font, Font.BOLD, 13));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);

        // Basic stats
        final DecimalFormat formatter = new DecimalFormat("#,###");

        final JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Capital
        if (country.getCapital().isPresent()) {
            final JLabel capitalLabel = new JLabel("Capital: " + country.getCapital().get());
            capitalLabel.setFont(new Font(font, Font.PLAIN, 11));
            capitalLabel.setForeground(Color.DARK_GRAY);
            statsPanel.add(capitalLabel);
        }

        // Population
        final JLabel popLabel = new JLabel(" | Pop: " + formatter.format(country.getPopulation()));
        popLabel.setFont(new Font(font, Font.PLAIN, 11));
        popLabel.setForeground(Color.DARK_GRAY);
        statsPanel.add(popLabel);

        // Area
        if (country.getAreaKm2() > 0) {
            final JLabel areaLabel = new JLabel(" | Area: " + formatter.format(country.getAreaKm2()) + " km^2");
            areaLabel.setFont(new Font(font, Font.PLAIN, 11));
            areaLabel.setForeground(Color.DARK_GRAY);
            statsPanel.add(areaLabel);
        }

        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(statsPanel);
        return infoPanel;
    }

    @NotNull
    private JPanel createCountryCardFlagPanel(Country country) {
        // Flag image
        final ImageIcon flagIcon = loadFlag(country, 60, 40);
        final JLabel flagLabel;
        if (flagIcon != null) {
            flagLabel = new JLabel(flagIcon);
        }
        else {
            flagLabel = new JLabel("No flag");
            flagLabel.setForeground(Color.GRAY);
        }
        flagLabel.setHorizontalAlignment(SwingConstants.CENTER);
        flagLabel.setVerticalAlignment(SwingConstants.CENTER);

        final JPanel flagPanel = new JPanel(new BorderLayout());
        flagPanel.setBackground(Color.WHITE);
        flagPanel.add(flagLabel, BorderLayout.CENTER);
        flagPanel.setPreferredSize(new Dimension(70, 50));
        return flagPanel;
    }

    private ImageIcon loadFlag(Country country, int width, int height) {
        ImageIcon outputIcon;
        try {
            final Image img = new ImageIcon(new URL(country.getFlagUrl())).getImage()
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);
            outputIcon = new ImageIcon(img);
        }
        catch (MalformedURLException exception) {
            outputIcon = null;
        }
        return outputIcon;
    }
}
