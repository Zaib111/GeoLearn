package app.views.collection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

import app.Navigator;
import app.controllers.CollectionController;
import app.entities.Country;
import app.entities.CountryCollection;
import app.views.AbstractView;
import app.views.ViewModel;

public class CollectionView extends AbstractView {
    private final CollectionController collectionController;
    private final JTextField collectionNameField;
    private final JTextField countriesField;
    private final JButton createButton;
    private final JPanel inputPanel;
    private final JPanel collectionsPanel;
    private final JScrollPane collectionsScrollPane;

    private final Navigator navigator;

    public CollectionView(ViewModel<CollectionState> collectionViewModel, CollectionController collectionController, Navigator navigator) {
        super(collectionViewModel);

        this.collectionController = collectionController;
        this.navigator = navigator;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create main content panel
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Title
        final JLabel titleLabel = new JLabel("Collections");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Input panel for creating new collection
        inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Create New Collection",
                TitledBorder.LEFT,
                TitledBorder.TOP
        ));

        // Collection name input
        final JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(Color.WHITE);
        final JLabel nameLabel = new JLabel("Collection Name:");
        collectionNameField = new JTextField(20);
        namePanel.add(nameLabel);
        namePanel.add(collectionNameField);
        inputPanel.add(namePanel);

        // Countries input (comma-separated)
        final JPanel countriesInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countriesInputPanel.setBackground(Color.WHITE);
        final JLabel countriesLabel = new JLabel("Countries (comma-separated):");
        countriesField = new JTextField(30);
        countriesInputPanel.add(countriesLabel);
        countriesInputPanel.add(countriesField);
        inputPanel.add(countriesInputPanel);

        // Help text
        final JLabel helpLabel = new JLabel("Example: Canada, United States, Mexico");
        helpLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        helpLabel.setForeground(Color.GRAY);
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        final JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        helpPanel.setBackground(Color.WHITE);
        helpPanel.add(helpLabel);
        inputPanel.add(helpPanel);

        // Create button
        createButton = new JButton("Create Collection");
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createButton);
        inputPanel.add(buttonPanel);

        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Collections display panel
        final JLabel collectionsTitle = new JLabel("Your Collections");
        collectionsTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        collectionsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(collectionsTitle);
        contentPanel.add(Box.createVerticalStrut(10));

        collectionsPanel = new JPanel();
        collectionsPanel.setLayout(new BoxLayout(collectionsPanel, BoxLayout.Y_AXIS));
        collectionsPanel.setBackground(Color.WHITE);

        collectionsScrollPane = new JScrollPane(collectionsPanel);
        collectionsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        collectionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        collectionsScrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        collectionsScrollPane.setPreferredSize(new Dimension(600, 300));

        final JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
        scrollPaneWrapper.setBackground(Color.WHITE);
        scrollPaneWrapper.add(collectionsScrollPane, BorderLayout.CENTER);
        contentPanel.add(scrollPaneWrapper);

        add(contentPanel, BorderLayout.CENTER);

        // Add action listener for create button
        createButton.addActionListener(e -> handleCreateCollection());
    }

    private void handleCreateCollection() {
        String collectionName = collectionNameField.getText().trim();
        String countriesInput = countriesField.getText().trim();

        if (collectionName.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a collection name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (countriesInput.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter at least one country name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Parse countries from comma-separated input
        List<String> countryNames = new ArrayList<>();
        String[] countryNamesArray = countriesInput.split(",");
        for (String countryName : countryNamesArray) {
            String trimmedName = countryName.trim();
            if (!trimmedName.isEmpty()) {
                countryNames.add(trimmedName);
            }
        }

        if (countryNames.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter at least one valid country name.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        collectionController.addCollection(collectionName, countryNames);
        // Clear input fields on successful creation (will be done via state update)
        collectionNameField.setText("");
        countriesField.setText("");
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
        CollectionState state = (CollectionState) newState;

        // Update collections display
        updateCollectionsDisplay(state.getAllCollections());
    }


    private void updateCollectionsDisplay(List<CountryCollection> collections) {
        collectionsPanel.removeAll();

        if (collections == null || collections.isEmpty()) {
            JLabel emptyLabel = new JLabel("No collections yet. Create one above!");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            collectionsPanel.add(emptyLabel);
        } else {
            for (CountryCollection collection : collections) {
                JPanel collectionPanel = createCollectionPanel(collection);
                collectionsPanel.add(collectionPanel);
                collectionsPanel.add(Box.createVerticalStrut(10));
            }
        }

        collectionsPanel.revalidate();
        collectionsPanel.repaint();
    }

    private JPanel createCollectionPanel(CountryCollection collection) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Collection name with rename button
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel(collection.getCollectionName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        namePanel.add(nameLabel);

        JButton renameButton = new JButton("Rename");
        renameButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        renameButton.addActionListener(e -> handleRenameCollection(collection));
        namePanel.add(renameButton);

        panel.add(namePanel);
        panel.add(Box.createVerticalStrut(5));

        // Display countries with flags and stats
        List<Country> countries = collection.getCountries();
        if (countries != null && !countries.isEmpty()) {
            // Create a panel to hold country cards
            JPanel countriesPanel = new JPanel();
            countriesPanel.setLayout(new BoxLayout(countriesPanel, BoxLayout.Y_AXIS));
            countriesPanel.setBackground(Color.WHITE);

            for (Country country : countries) {
                JPanel countryCard = createCountryCard(country);
                countriesPanel.add(countryCard);
                countriesPanel.add(Box.createVerticalStrut(5));
            }

            JScrollPane countriesScroll = new JScrollPane(countriesPanel);
            countriesScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            countriesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            countriesScroll.setPreferredSize(new Dimension(450, Math.min(200, countries.size() * 80)));
            countriesScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            countriesScroll.setAlignmentX(Component.LEFT_ALIGNMENT);

            panel.add(countriesScroll);
        } else {
            JLabel emptyLabel = new JLabel("No countries in this collection");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(emptyLabel);
        }

        panel.add(Box.createVerticalStrut(5));

        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);

        JButton editButton = new JButton("Edit");
        editButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        editButton.addActionListener(e -> handleEditCollection(collection));
        buttonPanel.add(editButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(e -> handleDeleteCollection(collection));
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel);

        return panel;
    }

    private void handleRenameCollection(CountryCollection collection) {
        String newName = JOptionPane.showInputDialog(
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
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Collection", true);
        editDialog.setLayout(new BorderLayout(10, 10));
        editDialog.setSize(500, 400);

        // Center the dialog
        editDialog.setLocationRelativeTo(this);

        // Current countries display
        JPanel currentPanel = new JPanel(new BorderLayout());
        currentPanel.setBorder(BorderFactory.createTitledBorder("Current Countries"));
        JTextArea currentCountries = new JTextArea();
        currentCountries.setEditable(false);
        currentCountries.setFont(new Font("SansSerif", Font.PLAIN, 12));

        List<Country> countries = collection.getCountries();
        if (countries != null && !countries.isEmpty()) {
            String countriesText = countries.stream()
                    .map(Country::getName)
                    .collect(Collectors.joining("\n"));
            currentCountries.setText(countriesText);
        }

        JScrollPane currentScroll = new JScrollPane(currentCountries);
        currentScroll.setPreferredSize(new Dimension(400, 150));
        currentPanel.add(currentScroll, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add/Remove Countries"));

        JLabel addLabel = new JLabel("Countries to add (comma-separated):");
        JTextField addField = new JTextField(30);

        JLabel removeLabel = new JLabel("Countries to remove (comma-separated):");
        JTextField removeField = new JTextField(30);

        inputPanel.add(addLabel);
        inputPanel.add(addField);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(removeLabel);
        inputPanel.add(removeField);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            List<String> toAdd = parseCountryNames(addField.getText());
            List<String> toRemove = parseCountryNames(removeField.getText());

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
        });

        cancelButton.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editDialog.add(currentPanel, BorderLayout.NORTH);
        editDialog.add(inputPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);

        editDialog.setVisible(true);
    }

    private void handleDeleteCollection(CountryCollection collection) {
        int option = JOptionPane.showConfirmDialog(
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
        List<String> countryNames = new ArrayList<>();
        if (input != null && !input.trim().isEmpty()) {
            String[] names = input.split(",");
            for (String name : names) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    countryNames.add(trimmed);
                }
            }
        }
        return countryNames;
    }

    private JPanel createCountryCard(Country country) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(5, 5, 5, 5)
        ));
        card.setBackground(Color.WHITE);

        // Flag image
        ImageIcon flagIcon = loadFlag(country, 60, 40);
        JLabel flagLabel;
        if (flagIcon != null) {
            flagLabel = new JLabel(flagIcon);
        } else {
            flagLabel = new JLabel("No flag");
            flagLabel.setForeground(Color.GRAY);
        }
        flagLabel.setHorizontalAlignment(SwingConstants.CENTER);
        flagLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel flagPanel = new JPanel(new BorderLayout());
        flagPanel.setBackground(Color.WHITE);
        flagPanel.add(flagLabel, BorderLayout.CENTER);
        flagPanel.setPreferredSize(new Dimension(70, 50));

        // Country info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        // Country name
        JLabel nameLabel = new JLabel(country.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);

        // Basic stats
        DecimalFormat formatter = new DecimalFormat("#,###");

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Capital
        if (country.getCapital().isPresent()) {
            JLabel capitalLabel = new JLabel("Capital: " + country.getCapital().get());
            capitalLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            capitalLabel.setForeground(Color.DARK_GRAY);
            statsPanel.add(capitalLabel);
        }

        // Population
        JLabel popLabel = new JLabel(" | Pop: " + formatter.format(country.getPopulation()));
        popLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        popLabel.setForeground(Color.DARK_GRAY);
        statsPanel.add(popLabel);

        // Area
        if (country.getAreaKm2() > 0) {
            JLabel areaLabel = new JLabel(" | Area: " + formatter.format(country.getAreaKm2()) + " km²");
            areaLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            areaLabel.setForeground(Color.DARK_GRAY);
            statsPanel.add(areaLabel);
        }

        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(statsPanel);

        card.add(flagPanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);

        // Hyperlink implementation
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    String countryCode = country.getCode();
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

    private ImageIcon loadFlag(Country country, int width, int height) {
        try {
            Image img = new ImageIcon(new URL(country.getFlagUrl())).getImage()
                    .getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            return null;
        }
    }
}
