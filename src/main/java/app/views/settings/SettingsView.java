package app.views.settings;

import app.controllers.SettingsController;
import app.use_cases.settings.UserSettingsData;
import app.views.AbstractView;
import app.views.ViewModel;

import javax.swing.*;
import java.awt.*;

public class SettingsView extends AbstractView {
    public final SettingsController settingsController;
    private final JTextField usernameField;
    private final JLabel messageLabel;
    private final JLabel currentUsernameLabel;
    private final JPanel inputPanel;
    private final JButton saveButton;

    public SettingsView(ViewModel<SettingsState> settingsViewModel, SettingsController settingsController) {
        super(settingsViewModel);

        this.settingsController = settingsController;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        // We generate the UI components.
        // Note not all data is populated yet as we still
        // need to fetch it via the controller.
        // This happens in the onViewOpened method.

        // Create a panel for centering content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        add(Box.createVerticalGlue());

        // Title
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Current username display
        currentUsernameLabel = new JLabel("Current Username: ");
        currentUsernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        currentUsernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(currentUsernameLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Username input panel
        inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(Color.WHITE);
        JLabel usernameLabel = new JLabel("New Username:");
        usernameField = new JTextField(20);
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Save button
        saveButton = new JButton("Save");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(saveButton);
        contentPanel.add(Box.createVerticalStrut(10));

        // Message label
        messageLabel = new JLabel("");
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageLabel.setVisible(false);
        contentPanel.add(messageLabel);

        add(contentPanel);
        add(Box.createVerticalGlue());

        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText();
            UserSettingsData newSettings = new UserSettingsData(newUsername);
            settingsController.changeUserSettings(newSettings);
        });
    }

    @Override
    public void onViewOpened() {
        // The first thing that happens after view is opened
        // Calls the controller to fetch current settings
        settingsController.fetchSettings();
        // This causes the use case interactor to fetch the settings
        // The presenter will then update the view model accordingly
        // When the presenter updates the view model,
        // The onStateChange method will automatically be called
        // as soon as the state of the view model changes.
        // This is where we will populate the UI with the fetched settings.
    }

    @Override
    public void onViewClosed() {

    }

    @Override
    public void onStateChange(Object oldState, Object newState) {
        // Update UI based on new state
        SettingsState settingsState = (SettingsState) newState;

        // Check if there's an error - if so, hide input and save button
        boolean hasError = settingsState.getErrorMessage() != null && !settingsState.getErrorMessage().isEmpty();
        inputPanel.setVisible(!hasError);
        saveButton.setVisible(!hasError);

        // Update current username display
        String username = settingsState.getUsername();
        if (username != null && !username.isEmpty()) {
            currentUsernameLabel.setText("Current Username: " + username);
            currentUsernameLabel.setVisible(!hasError);
        } else {
            currentUsernameLabel.setText("Current Username: (not set)");
            currentUsernameLabel.setVisible(!hasError);
        }

        // Update username field
        usernameField.setText(settingsState.getUsername());

        // Handle success message
        if (settingsState.getSuccessMessage() != null && !settingsState.getSuccessMessage().isEmpty()) {
            messageLabel.setText(settingsState.getSuccessMessage());
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setVisible(true);
        }
        // Handle error message
        else if (hasError) {
            messageLabel.setText(settingsState.getErrorMessage());
            messageLabel.setForeground(Color.RED);
            messageLabel.setVisible(true);
        }
        // Hide message if both are null or empty
        else {
            messageLabel.setVisible(false);
        }
    }
}
