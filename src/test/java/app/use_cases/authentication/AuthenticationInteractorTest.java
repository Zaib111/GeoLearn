package app.use_cases.authentication;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for AuthenticationInteractor with 100% code coverage.
 * Uses manual mocking of dependencies.
 */
class AuthenticationInteractorTest {

    private AuthenticationInteractor interactor;
    private MockAuthenticationOutputBoundary mockOutputBoundary;
    private MockAuthenticationDataAccess mockDataAccess;

    @BeforeEach
    void setUp() {
        mockOutputBoundary = new MockAuthenticationOutputBoundary();
        mockDataAccess = new MockAuthenticationDataAccess();
        interactor = new AuthenticationInteractor(mockOutputBoundary, mockDataAccess);
    }

    // ==================== SIGN IN TESTS ====================

    @Test
    void testSignIn_Success() {
        // Arrange
        AuthenticationData loginUser = new AuthenticationData("testuser", "password123");
        AuthenticationData storedUser = new AuthenticationData("testuser", "password123");
        mockDataAccess.addUser(storedUser);

        // Act
        interactor.signIn(loginUser);

        // Assert
        assertNull(mockOutputBoundary.lastError, "No error should be presented");
        assertNotNull(mockOutputBoundary.lastRedirectedUser, "Should redirect to home");
        assertEquals("testuser", mockOutputBoundary.lastRedirectedUser.getUsername());
        assertEquals("testuser", mockDataAccess.currentUser.getUsername());
    }

    @Test
    void testSignIn_UserNotFound() {
        // Arrange
        AuthenticationData loginUser = new AuthenticationData("nonexistent", "password123");

        // Act
        interactor.signIn(loginUser);

        // Assert
        assertEquals("User not found", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
        assertNull(mockDataAccess.currentUser, "Should not set current user");
    }

    @Test
    void testSignIn_UserNotFound_EmptyUsername() {
        // Arrange
        AuthenticationData loginUser = new AuthenticationData("testuser", "password123");
        // Mock returns user with empty username (not found)
        mockDataAccess.returnEmptyUser = true;

        // Act
        interactor.signIn(loginUser);

        // Assert
        assertEquals("User not found", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignIn_IncorrectPassword() {
        // Arrange
        AuthenticationData loginUser = new AuthenticationData("testuser", "wrongpassword");
        AuthenticationData storedUser = new AuthenticationData("testuser", "correctpassword");
        mockDataAccess.addUser(storedUser);

        // Act
        interactor.signIn(loginUser);

        // Assert
        assertEquals("Incorrect password", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
        assertNull(mockDataAccess.currentUser, "Should not set current user");
    }

    // ==================== SIGN UP TESTS ====================

    @Test
    void testSignUp_Success() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("newuser", "password123");
        newUser.setConfirmPassword("password123");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertNull(mockOutputBoundary.lastError, "No error should be presented");
        assertNotNull(mockOutputBoundary.lastRedirectedUser, "Should redirect to home");
        assertEquals("newuser", mockOutputBoundary.lastRedirectedUser.getUsername());
        assertTrue(mockDataAccess.savedUsers.contains(newUser), "User should be saved");
        assertEquals("newuser", mockDataAccess.currentUser.getUsername());
    }

    @Test
    void testSignUp_Success_WithWhitespace() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("  newuser  ", "password123");
        newUser.setConfirmPassword("password123");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertNull(mockOutputBoundary.lastError, "No error should be presented");
        assertNotNull(mockOutputBoundary.lastRedirectedUser, "Should redirect to home");
        assertEquals("newuser", mockOutputBoundary.lastRedirectedUser.getUsername());
        assertEquals("newuser", newUser.getUsername(), "Username should be trimmed");
    }

    @Test
    void testSignUp_BlankUsername() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("", "password123");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Username cannot be blank", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
        assertTrue(mockDataAccess.savedUsers.isEmpty(), "User should not be saved");
    }

    @Test
    void testSignUp_BlankUsername_OnlyWhitespace() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("   ", "password123");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Username cannot be blank", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignUp_NullUsername() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData(null, "password123");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Username cannot be blank", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignUp_UsernameTooShort() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("ab", "password123");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Username must be at least 3 characters", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignUp_BlankPassword() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("validuser", "");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Password cannot be blank", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignUp_NullPassword() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("validuser", null);

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Password cannot be blank", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignUp_PasswordTooShort() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("validuser", "12345");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Password must be at least 6 characters", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignUp_PasswordsDoNotMatch() {
        // Arrange
        AuthenticationData newUser = new AuthenticationData("validuser", "password123");
        newUser.setConfirmPassword("differentpassword");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Passwords do not match", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
    }

    @Test
    void testSignUp_UsernameAlreadyExists() {
        // Arrange
        AuthenticationData existingUser = new AuthenticationData("existinguser", "password123");
        mockDataAccess.addUser(existingUser);

        AuthenticationData newUser = new AuthenticationData("existinguser", "password456");

        // Act
        interactor.signUp(newUser);

        // Assert
        assertEquals("Username already exists", mockOutputBoundary.lastError);
        assertNull(mockOutputBoundary.lastRedirectedUser, "Should not redirect to home");
        assertEquals(1, mockDataAccess.savedUsers.size(), "Should only have one user");
    }

    @Test
    void testSignUp_NoConfirmPassword() {
        // Arrange - confirmPassword is null, should not trigger validation error
        AuthenticationData newUser = new AuthenticationData("validuser", "password123");
        // confirmPassword is null by default

        // Act
        interactor.signUp(newUser);

        // Assert
        assertNull(mockOutputBoundary.lastError, "No error should be presented when confirmPassword is null");
        assertNotNull(mockOutputBoundary.lastRedirectedUser, "Should redirect to home");
    }

    // ==================== CONTINUE AS GUEST TESTS ====================

    @Test
    void testContinueAsGuest() {
        // Act
        interactor.continueAsGuest();

        // Assert
        assertNull(mockOutputBoundary.lastError, "No error should be presented");
        assertNotNull(mockOutputBoundary.lastRedirectedUser, "Should redirect to home");
        assertEquals("", mockOutputBoundary.lastRedirectedUser.getUsername(), "Guest user should have empty username");
        assertEquals("", mockOutputBoundary.lastRedirectedUser.getPassword(), "Guest user should have empty password");
        assertNotNull(mockDataAccess.currentUser, "Current user should be set");
        assertEquals("", mockDataAccess.currentUser.getUsername());
    }

    // ==================== MOCK CLASSES ====================

    /**
     * Mock implementation of AuthenticationOutputBoundary for testing.
     */
    private static class MockAuthenticationOutputBoundary implements AuthenticationOutputBoundary {
        AuthenticationData lastRedirectedUser = null;
        String lastError = null;

        @Override
        public void redirectToHome(AuthenticationData userData) {
            this.lastRedirectedUser = userData;
        }

        @Override
        public void presentError(String errorMessage) {
            this.lastError = errorMessage;
        }
    }

    /**
     * Mock implementation of AuthenticationDataAccessInterface for testing.
     */
    private static class MockAuthenticationDataAccess implements AuthenticationDataAccessInterface {
        List<AuthenticationData> savedUsers = new ArrayList<>();
        AuthenticationData currentUser = null;
        boolean returnEmptyUser = false;

        void addUser(AuthenticationData user) {
            savedUsers.add(user);
        }

        @Override
        public AuthenticationData getUserAuth(AuthenticationData user) {
            if (returnEmptyUser) {
                return new AuthenticationData("", "");
            }

            for (AuthenticationData savedUser : savedUsers) {
                if (savedUser.getUsername().equals(user.getUsername())) {
                    return savedUser;
                }
            }
            // Return empty user if not found
            return new AuthenticationData("", "");
        }

        @Override
        public void saveUserToDatabase(AuthenticationData user) {
            savedUsers.add(user);
        }

        @Override
        public void setCurrentUser(AuthenticationData user) {
            this.currentUser = user;
        }
    }
}
