package app.controllers;

import app.use_cases.authentication.AuthenticationInputBoundary;
import app.use_cases.authentication.AuthenticationData;

/**
 * Controller for handling authentication-related user interactions.
 */
public class AuthenticationController {
    private final AuthenticationInputBoundary authenticationInputBoundary;

    /**
     * Constructs an AuthenticationController.
     *
     * @param authenticationInputBoundary the authentication input boundary
     */
    public AuthenticationController(AuthenticationInputBoundary authenticationInputBoundary) {
        this.authenticationInputBoundary = authenticationInputBoundary;
    }

    /**
     * Authenticates a user with their credentials.
     *
     * @param authData the user authentication data
     */
    public void signIn(AuthenticationData authData) {
        authenticationInputBoundary.signIn(authData);
    }

    /**
     * Registers a new user account.
     *
     * @param authData the new user authentication data
     */
    public void signUp(AuthenticationData authData) {
        authenticationInputBoundary.signUp(authData);
    }

    /**
     * Continues as a guest user.
     */
    public void continueAsGuest() {
        authenticationInputBoundary.continueAsGuest();
    }
}
