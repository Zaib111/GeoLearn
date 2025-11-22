package app.views.settings;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import app.controllers.SettingsController;
import app.use_cases.settings.UserSettingsData;
import app.views.AbstractView;
import app.views.ViewModel;

/**
 * Settings view for managing user settings.
 */
public class SettingsView extends AbstractView {
    private static final int TITLE_FONT_SIZE = 24;
    private static final int LABEL_FONT_SIZE = 14;
    private static final int USERNAME_FIELD_WIDTH = 20;
    private static final int VERTICAL_STRUT_LARGE = 20;
    private static final int VERTICAL_STRUT_SMALL = 10;

    private final SettingsController settingsController;
    private final JTextField usernameField;
    private final JLabel messageLabel;
    private final JLabel currentUsernameLabel;
    private final JPanel inputPanel;
    private final JButton saveButton;

    /**
     * Constructs a SettingsView.
     *
     * @param settingsViewModel the view model for settings
     * @param controller         the settings controller
     */
    public SettingsView(ViewModel<SettingsState> settingsViewModel,
                        SettingsController controller) {
        super(settingsViewModel);

        this.settingsController = controller;
        this.usernameField = new JTextField(USERNAME_FIELD_WIDTH);
        this.messageLabel = createMessageLabel();
        this.currentUsernameLabel = createCurrentUsernameLabel();
        this.inputPanel = createInputPanel();
        this.saveButton = createSaveButton();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        final JPanel contentPanel = createContentPanel();

        add(Box.createVerticalGlue());
        add(contentPanel);
        add(Box.createVerticalGlue());

        setupSaveButtonListener();
    }

    private JLabel createCurrentUsernameLabel() {
        final JLabel label = new JLabel("Current Username: ");
        label.setFont(new Font("SansSerif", Font.PLAIN, LABEL_FONT_SIZE));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel createInputPanel() {
        final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        final JLabel usernameLabel = new JLabel("New Username:");
        panel.add(usernameLabel);
        panel.add(usernameField);
        return panel;
    }

    private JButton createSaveButton() {
        final JButton button = new JButton("Save");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JLabel createMessageLabel() {
        final JLabel label = new JLabel("");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setVisible(false);
        return label;
    }

    private JPanel createContentPanel() {
        final JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createTitleLabel());
        contentPanel.add(Box.createVerticalStrut(VERTICAL_STRUT_LARGE));

        contentPanel.add(currentUsernameLabel);
        contentPanel.add(Box.createVerticalStrut(VERTICAL_STRUT_LARGE));

        contentPanel.add(inputPanel);
        contentPanel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));

        contentPanel.add(saveButton);
        contentPanel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));

        contentPanel.add(messageLabel);

        return contentPanel;
    }

    private JLabel createTitleLabel() {
        final JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, TITLE_FONT_SIZE));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return titleLabel;
    }

    private void setupSaveButtonListener() {
        saveButton.addActionListener(event -> {
            final String newUsername = usernameField.getText();
            final UserSettingsData newSettings = new UserSettingsData(newUsername);
            settingsController.changeUserSettings(newSettings);
        });
    }

    /**
     * Gets the settings controller.
     *
     * @return the settings controller
     */
    public SettingsController getSettingsController() {
        return settingsController;
    }

    @Override
    public void onViewOpened() {
        settingsController.fetchSettings();
    }

    @Override
    public void onViewClosed() {
        // No cleanup needed
    }

    @Override
    public void onStateChange(Object oldState, Object newState) {
        final SettingsState settingsState = (SettingsState) newState;

        final boolean hasError = settingsState.getErrorMessage() != null
                && !settingsState.getErrorMessage().isEmpty();
        inputPanel.setVisible(!hasError);
        saveButton.setVisible(!hasError);

        final String username = settingsState.getUsername();
        if (username != null && !username.isEmpty()) {
            currentUsernameLabel.setText("Current Username: " + username);
            currentUsernameLabel.setVisible(!hasError);
        }
        else {
            currentUsernameLabel.setText("Current Username: (not set)");
            currentUsernameLabel.setVisible(!hasError);
        }

        usernameField.setText(settingsState.getUsername());

        if (settingsState.getSuccessMessage() != null
                && !settingsState.getSuccessMessage().isEmpty()) {
            messageLabel.setText(settingsState.getSuccessMessage());
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setVisible(true);
        }
        else if (hasError) {
            messageLabel.setText(settingsState.getErrorMessage());
            messageLabel.setForeground(Color.RED);
            messageLabel.setVisible(true);
        }
        else {
            messageLabel.setVisible(false);
        }
    }
}
