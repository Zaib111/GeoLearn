package app.use_cases.authentication;

/**
 * Input boundary interface for authentication use case.
 */
public interface AuthenticationInputBoundary {
    /**
     * Authenticates a user with their credentials.
     *
     * @param user the user authentication data
     */
    void signIn(AuthenticationData user);

    /**
     * Registers a new user account.
     *
     * @param user the new user authentication data
     */
    void signUp(AuthenticationData user);

    /**
     * Continues as a guest user (no authentication).
     * Sets an empty user as current and redirects to home.
     */
    void continueAsGuest();
}
