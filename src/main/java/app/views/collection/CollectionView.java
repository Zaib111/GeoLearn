package app.views.collection;

import app.controllers.CollectionController;
import app.entities.Country;
import app.entities.CountryCollection;
import app.views.AbstractView;
import app.views.ViewModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CollectionView extends AbstractView {
    private final CollectionController collectionController;
    private final JTextField collectionNameField;
    private final JTextField countriesField;
    private final JButton createButton;
    private final JPanel inputPanel;
    private final JPanel collectionsPanel;
    private final JScrollPane collectionsScrollPane;

    public CollectionView(ViewModel<CollectionState> collectionViewModel, CollectionController collectionController) {
        super(collectionViewModel);

        this.collectionController = collectionController;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Collections");
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
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel("Collection Name:");
        collectionNameField = new JTextField(20);
        namePanel.add(nameLabel);
        namePanel.add(collectionNameField);
        inputPanel.add(namePanel);

        // Countries input (comma-separated)
        JPanel countriesInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        countriesInputPanel.setBackground(Color.WHITE);
        JLabel countriesLabel = new JLabel("Countries (comma-separated):");
        countriesField = new JTextField(30);
        countriesInputPanel.add(countriesLabel);
        countriesInputPanel.add(countriesField);
        inputPanel.add(countriesInputPanel);

        // Help text
        JLabel helpLabel = new JLabel("Example: Canada, United States, Mexico");
        helpLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        helpLabel.setForeground(Color.GRAY);
        helpLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        helpPanel.setBackground(Color.WHITE);
        helpPanel.add(helpLabel);
        inputPanel.add(helpPanel);

        // Create button
        createButton = new JButton("Create Collection");
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(createButton);
        inputPanel.add(buttonPanel);

        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Collections display panel
        JLabel collectionsTitle = new JLabel("Your Collections");
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

        JPanel scrollPaneWrapper = new JPanel(new BorderLayout());
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

        // Parse countries from comma-separated input
        List<Country> countries = new ArrayList<>();
        String[] countryNames = countriesInput.split(",");
        for (String countryName : countryNames) {
            String trimmedName = countryName.trim();
            countries.add(new Country("AAA", trimmedName, null, "", null, 0, 0.0, null, "", null, null, null));
        }

        collectionController.addCollection(collectionName, countries);
        collectionController.fetchAllCollections();
    }

    @Override
    public void onViewOpened() {
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

        // Collection name
        JLabel nameLabel = new JLabel(collection.getCollectionName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(5));

        // Display countries
        List<Country> countries = collection.getCountries();
        if (countries != null && !countries.isEmpty()) {
            String countriesText = countries.stream()
                    .map(Country::getName)
                    .collect(Collectors.joining(", "));

            JTextArea countriesLabel = new JTextArea(countriesText);
            countriesLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            countriesLabel.setWrapStyleWord(true);
            countriesLabel.setLineWrap(true);
            countriesLabel.setEditable(false);
            countriesLabel.setBackground(Color.WHITE);
            countriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(countriesLabel);
        } else {
            JLabel emptyLabel = new JLabel("No countries in this collection");
            emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(emptyLabel);
        }
        return panel;
    }
}
