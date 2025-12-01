package app.views.authentication;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import app.Navigator;
import app.controllers.AuthenticationController;
import app.use_cases.authentication.AuthenticationData;
import app.views.AbstractView;
import app.views.ViewModel;

/**
 * Sign-in/Sign-up view for user authentication.
 */
public class AuthenticationView extends AbstractView {
    private static final int TITLE_FONT_SIZE = 24;
    private static final int LABEL_FONT_SIZE = 14;
    private static final int FIELD_WIDTH = 20;
    private static final int VERTICAL_STRUT_LARGE = 20;
    private static final int VERTICAL_STRUT_SMALL = 10;

    private final AuthenticationController authenticationController;
    private final Navigator navigator;

    // Sign-in components
    private final JPanel signInPanel;
    private final JTextField signInUsernameField;
    private final JPasswordField signInPasswordField;
    private final JButton signInButton;
    private final JButton switchToSignUpButton;
    private final JButton signInGuestButton;

    // Sign-up components
    private final JPanel signUpPanel;
    private final JTextField signUpUsernameField;
    private final JPasswordField signUpPasswordField;
    private final JPasswordField signUpRepeatPasswordField;
    private final JButton signUpButton;
    private final JButton switchToSignInButton;
    private final JButton signUpGuestButton;

    // Shared components
    private final JLabel messageLabel;

    private ViewModel<AuthenticationState> authenticationViewModel;

    /**
     * Constructs an AuthenticationView.
     *
     * @param authenticationViewModel the view model for authentication
     * @param controller the authentication controller
     * @param navigator the navigator for view transitions
     */
    public AuthenticationView(ViewModel<AuthenticationState> authenticationViewModel,
                              AuthenticationController controller,
                              Navigator navigator) {
        super(authenticationViewModel);

        this.authenticationController = controller;
        this.navigator = navigator;
        this.authenticationViewModel = authenticationViewModel;

        // Initialize sign-in components
        this.signInUsernameField = new JTextField(FIELD_WIDTH);
        this.signInPasswordField = new JPasswordField(FIELD_WIDTH);
        this.signInButton = new JButton("Sign In");
        this.switchToSignUpButton = new JButton("Go to Sign Up");
        this.signInGuestButton = new JButton("Continue as Guest");

        // Initialize sign-up components
        this.signUpUsernameField = new JTextField(FIELD_WIDTH);
        this.signUpPasswordField = new JPasswordField(FIELD_WIDTH);
        this.signUpRepeatPasswordField = new JPasswordField(FIELD_WIDTH);
        this.signUpButton = new JButton("Sign Up");
        this.switchToSignInButton = new JButton("Back to Sign In");
        this.signUpGuestButton = new JButton("Continue as Guest");

        this.messageLabel = createMessageLabel();

        this.signInPanel = createSignInPanel();
        this.signUpPanel = createSignUpPanel();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        add(Box.createVerticalGlue());
        add(signInPanel);
        add(signUpPanel);
        add(Box.createVerticalGlue());

        setupButtonListeners();
    }

    private JLabel createMessageLabel() {
        final JLabel label = new JLabel("");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setVisible(false);
        return label;
    }

    private JPanel createSignInPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Title
        final JLabel titleLabel = new JLabel("Sign In");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, TITLE_FONT_SIZE));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create a sub-panel to group input fields together
        final JPanel inputGroup = new JPanel();
        inputGroup.setLayout(new BoxLayout(inputGroup, BoxLayout.Y_AXIS));
        inputGroup.setBackground(Color.WHITE);
        inputGroup.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 80));

        // Username field
        final JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 35));
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(signInUsernameField);

        // Password field
        final JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 35));
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(signInPasswordField);

        inputGroup.add(usernamePanel);
        inputGroup.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        inputGroup.add(passwordPanel);

        // Buttons
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        switchToSignUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInGuestButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_LARGE));
        panel.add(inputGroup);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_LARGE));
        panel.add(signInButton);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        panel.add(switchToSignUpButton);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        panel.add(signInGuestButton);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        panel.add(messageLabel);

        return panel;
    }

    private JPanel createSignUpPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        // Title
        final JLabel titleLabel = new JLabel("Sign Up");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, TITLE_FONT_SIZE));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Requirements label - wrapped in a centered panel for proper alignment
        final JLabel requirementsLabel = new JLabel("<html><center>Username: min 3 characters<br>Password: min 6 characters</center></html>");
        requirementsLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        requirementsLabel.setForeground(new Color(100, 100, 100));

        final JPanel requirementsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        requirementsPanel.setBackground(Color.WHITE);
        requirementsPanel.add(requirementsLabel);
        requirementsPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 35));

        // Create a sub-panel to group input fields together
        final JPanel inputGroup = new JPanel();
        inputGroup.setLayout(new BoxLayout(inputGroup, BoxLayout.Y_AXIS));
        inputGroup.setBackground(Color.WHITE);
        inputGroup.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 130));

        // Username field
        final JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 35));
        usernamePanel.add(new JLabel("Username:"));
        usernamePanel.add(signUpUsernameField);

        // Password field
        final JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 35));
        passwordPanel.add(new JLabel("Password:"));
        passwordPanel.add(signUpPasswordField);

        // Repeat password field
        final JPanel repeatPasswordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        repeatPasswordPanel.setBackground(Color.WHITE);
        repeatPasswordPanel.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 35));
        repeatPasswordPanel.add(new JLabel("Repeat Password:"));
        repeatPasswordPanel.add(signUpRepeatPasswordField);

        inputGroup.add(usernamePanel);
        inputGroup.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        inputGroup.add(passwordPanel);
        inputGroup.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        inputGroup.add(repeatPasswordPanel);

        // Buttons
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        switchToSignInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpGuestButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(requirementsPanel);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_LARGE));
        panel.add(inputGroup);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_LARGE));
        panel.add(signUpButton);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        panel.add(switchToSignInButton);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        panel.add(signUpGuestButton);
        panel.add(Box.createVerticalStrut(VERTICAL_STRUT_SMALL));
        panel.add(messageLabel);

        panel.setVisible(false); // Initially hidden

        return panel;
    }

    private void setupButtonListeners() {
        // Sign-in button
        signInButton.addActionListener(event -> {
            final String username = signInUsernameField.getText();
            final String password = new String(signInPasswordField.getPassword());
            final AuthenticationData authData = new AuthenticationData(username, password);
            authenticationController.signIn(authData);
        });

        // Sign-up button
        signUpButton.addActionListener(event -> {
            final String username = signUpUsernameField.getText();
            final String password = new String(signUpPasswordField.getPassword());
            final String repeatPassword = new String(signUpRepeatPasswordField.getPassword());

            final AuthenticationData authData = new AuthenticationData(username, password, repeatPassword);
            authenticationController.signUp(authData);
        });

        // Continue as guest buttons
        signInGuestButton.addActionListener(event -> {
            authenticationController.continueAsGuest();
        });

        signUpGuestButton.addActionListener(event -> {
            authenticationController.continueAsGuest();
        });

        // Switch to sign-up
        switchToSignUpButton.addActionListener(event -> {
            signInPanel.setVisible(false);
            signUpPanel.setVisible(true);
            messageLabel.setVisible(false);
            clearFields();

            // Update state to reflect sign-up mode
            final AuthenticationState state = new AuthenticationState();
            state.setSignUpMode(true);
            authenticationViewModel.updateState(state);
        });

        // Switch to sign-in
        switchToSignInButton.addActionListener(event -> {
            signUpPanel.setVisible(false);
            signInPanel.setVisible(true);
            messageLabel.setVisible(false);
            clearFields();

            // Update state to reflect sign-in mode
            final AuthenticationState state = new AuthenticationState();
            state.setSignUpMode(false);
            authenticationViewModel.updateState(state);
        });
    }

    private void clearFields() {
        signInUsernameField.setText("");
        signInPasswordField.setText("");
        signUpUsernameField.setText("");
        signUpPasswordField.setText("");
        signUpRepeatPasswordField.setText("");
    }

    @Override
    public void onViewOpened(String param) {
        authenticationViewModel.updateState(new AuthenticationState());
        clearFields();
    }

    @Override
    public void onViewClosed() {
        // No cleanup needed
    }

    @Override
    public void onStateChange(Object oldState, Object newState) {
        final AuthenticationState authenticationState = (AuthenticationState) newState;

        // Update visibility based on mode
        if (authenticationState.isSignUpMode()) {
            signInPanel.setVisible(false);
            signUpPanel.setVisible(true);
        }
        else {
            signInPanel.setVisible(true);
            signUpPanel.setVisible(false);
        }

        // Handle success - navigate to home
        if (authenticationState.isSuccess()) {
            navigator.navigateTo("home");
        }

        // Handle error messages
        if (authenticationState.getErrorMessage() != null
                && !authenticationState.getErrorMessage().isEmpty()) {
            messageLabel.setText(authenticationState.getErrorMessage());
            messageLabel.setForeground(Color.RED);
            messageLabel.setVisible(true);
        }
        else {
            messageLabel.setVisible(false);
        }
    }
}
