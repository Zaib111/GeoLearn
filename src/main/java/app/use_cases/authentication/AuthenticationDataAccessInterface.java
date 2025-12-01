package app.use_cases.authentication;

/**
 * Interface for accessing and persisting user authentication data.
 */
public interface AuthenticationDataAccessInterface {
    /**
     * Gets user authentication data from the database.
     *
     * @param user the user data containing the username to look up
     * @return the user authentication data from the database
     */
    AuthenticationData getUserAuth(AuthenticationData user);

    /**
     * Saves or updates a user in the database.
     * If the user doesn't exist, creates a new user.
     * If the user exists, updates their password.
     *
     * @param user the user data to save
     */
    void saveUserToDatabase(AuthenticationData user);

    /**
     * Sets the current user for this session.
     * This updates the local state without persisting to the database.
     *
     * @param user the user to set as current
     */
    void setCurrentUser(AuthenticationData user);
}
