package view;

import adapters.Collection.CollectionController;
import adapters.Collection.CollectionState;
import adapters.Collection.CollectionViewModel;
import entity.Country;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The View for when the user wants to view the collections page
 */
public class CollectionView extends JPanel implements ActionListener, PropertyChangeListener {
    private final CollectionViewModel collectionViewModel;
    private CollectionController collectionController;

    private final JTextField collectionNameField = new JTextField(20);
    private final JTextField countryNameField = new JTextField(20);
    private final JLabel errorLabel = new JLabel();
    private final JButton createButton = new JButton(CollectionViewModel.CREATE_COLLECTION_BUTTON_LABEL);

    public CollectionView(CollectionViewModel collectionViewModel) {
        this.collectionViewModel = collectionViewModel;
        collectionViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(CollectionViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel(CollectionViewModel.COLLECTION_LABEL), BorderLayout.WEST);
        namePanel.add(collectionNameField, BorderLayout.CENTER);

        JPanel countriesPanel = new JPanel(new BorderLayout());
        countriesPanel.add(new JLabel(CollectionViewModel.COUNTRIES_LABEL), BorderLayout.WEST);
        countriesPanel.add(countryNameField, BorderLayout.CENTER);

        add(title);
        add(namePanel);
        add(countriesPanel);
        add(createButton);
        add(errorLabel);

        createButton.addActionListener(this);
        addCollectionNameListener();
        addCountriesListener();
    }

    private void addCollectionNameListener() {
        collectionNameField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                CollectionState state = collectionViewModel.getState();
                state.setCollectionName(collectionNameField.getText());
                collectionViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { updateState(); }
            public void removeUpdate(DocumentEvent e) { updateState(); }
            public void changedUpdate(DocumentEvent e) { updateState(); }
        });
    }

    private void addCountriesListener() {
        countryNameField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateState() {
                CollectionState state = collectionViewModel.getState();
                String[] countryNames = countryNameField.getText().split(",");
                List<Country> countries = new ArrayList<>();
                for (String c : countryNames) {
                    if (!c.trim().isEmpty()) {
                        countries.add(new Country(
                            "", // code
                            c.trim(), // name
                            null, // capital
                            "", // region
                            null, // subregion
                            0L, // population
                            0.0, // areaKm2
                            new ArrayList<>(), // borders
                            "", // flagUrl
                            new ArrayList<>(), // languages
                            new ArrayList<>(), // currencies
                            new ArrayList<>()  // timezones
                        ));
                    }
                }
                state.setCountriesToAdd(countries);
                collectionViewModel.setState(state);
            }
            public void insertUpdate(DocumentEvent e) { updateState(); }
            public void removeUpdate(DocumentEvent e) { updateState(); }
            public void changedUpdate(DocumentEvent e) { updateState(); }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == createButton && collectionController != null) {
            CollectionState state = collectionViewModel.getState();
            collectionController.execute(state.getCollectionName(), state.getCountriesToAdd());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CollectionState state = (CollectionState) evt.getNewValue();
        errorLabel.setText(state.getCollectionError() != null ? state.getCollectionError() : "");
    }

    public void setCollectionController(CollectionController controller) {
        this.collectionController = controller;
    }

    // For testing/demo purposes
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CollectionViewModel viewModel = new CollectionViewModel();
            CollectionView view = new CollectionView(viewModel);
            JFrame frame = new JFrame("Collection View");
            frame.setContentPane(view);
            frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
