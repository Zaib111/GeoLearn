package app.use_cases.authentication;

/**
 * Interactor for authentication use case.
 */
public class AuthenticationInteractor implements AuthenticationInputBoundary {
    private final AuthenticationOutputBoundary authenticationOutputBoundary;
    private final AuthenticationDataAccessInterface authenticationDataAccessInterface;

    /**
     * Constructs an AuthenticationInteractor.
     *
     * @param authenticationOutputBoundary the output boundary for authentication
     * @param authenticationDataAccessInterface the data access interface for authentication
     */
    public AuthenticationInteractor(AuthenticationOutputBoundary authenticationOutputBoundary,
                                    AuthenticationDataAccessInterface authenticationDataAccessInterface) {
        this.authenticationOutputBoundary = authenticationOutputBoundary;
        this.authenticationDataAccessInterface = authenticationDataAccessInterface;
    }

    @Override
    public void signIn(AuthenticationData user) {
        // Get user from database to verify credentials
        final AuthenticationData storedUser = authenticationDataAccessInterface.getUserAuth(user);

        // Check if user exists
        if (storedUser.getUsername() == null || storedUser.getUsername().isEmpty()) {
            authenticationOutputBoundary.presentError("User not found");
            return;
        }

        // Verify password matches
        if (!storedUser.getPassword().equals(user.getPassword())) {
            authenticationOutputBoundary.presentError("Incorrect password");
            return;
        }

        // Set as current user if authentication is successful
        authenticationDataAccessInterface.setCurrentUser(storedUser);
        authenticationOutputBoundary.redirectToHome(storedUser);
    }

    @Override
    public void signUp(AuthenticationData user) {
        // Sanitize username (trim whitespace)
        final String sanitizedUsername = user.getUsername() != null ? user.getUsername().trim() : "";

        // Validate username is not blank
        if (sanitizedUsername.isEmpty()) {
            authenticationOutputBoundary.presentError("Username cannot be blank");
            return;
        }

        // Validate username length (minimum 3 characters)
        if (sanitizedUsername.length() < 3) {
            authenticationOutputBoundary.presentError("Username must be at least 3 characters");
            return;
        }

        // Validate password is not blank
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            authenticationOutputBoundary.presentError("Password cannot be blank");
            return;
        }

        // Validate password strength (minimum 6 characters)
        if (user.getPassword().length() < 6) {
            authenticationOutputBoundary.presentError("Password must be at least 6 characters");
            return;
        }

        // Validate password confirmation
        if (user.getConfirmPassword() != null && !user.getPassword().equals(user.getConfirmPassword())) {
            authenticationOutputBoundary.presentError("Passwords do not match");
            return;
        }

        // Update user with sanitized username
        user.setUsername(sanitizedUsername);

        // Check if user already exists
        final AuthenticationData existingUser = authenticationDataAccessInterface.getUserAuth(user);

        if (existingUser.getUsername() != null && !existingUser.getUsername().isEmpty()) {
            authenticationOutputBoundary.presentError("Username already exists");
            return;
        }

        // Save new user to database
        authenticationDataAccessInterface.saveUserToDatabase(user);

        // Set as current user
        authenticationDataAccessInterface.setCurrentUser(user);

        // Redirect to home
        authenticationOutputBoundary.redirectToHome(user);
    }

    @Override
    public void continueAsGuest() {
        // Set an empty user as current (guest mode)
        final AuthenticationData guestUser = new AuthenticationData("", "");
        authenticationDataAccessInterface.setCurrentUser(guestUser);

        // Redirect to home
        authenticationOutputBoundary.redirectToHome(guestUser);
    }
}
