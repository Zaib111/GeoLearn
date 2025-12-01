package app.use_cases.authentication;

/**
 * Output boundary interface for authentication use case.
 */
public interface AuthenticationOutputBoundary {
    /**
     * Handles successful authentication and redirects to home.
     *
     * @param userData the authenticated user data
     */
    void redirectToHome(AuthenticationData userData);

    /**
     * Presents an error message to the user.
     *
     * @param errorMessage the error message to display
     */
    void presentError(String errorMessage);
}
