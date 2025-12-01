package app.presenters;

import app.use_cases.authentication.AuthenticationOutputBoundary;
import app.use_cases.authentication.AuthenticationData;
import app.views.ViewModel;
import app.views.authentication.AuthenticationState;

public class AuthenticationPresenter implements AuthenticationOutputBoundary {
    private final ViewModel<AuthenticationState> authenticationViewModel;

    public AuthenticationPresenter(ViewModel<AuthenticationState> authenticationViewModel) {
        this.authenticationViewModel = authenticationViewModel;
    }

    @Override
    public void redirectToHome(AuthenticationData userData) {
        final AuthenticationState state = new AuthenticationState();
        state.setSuccess(true);
        authenticationViewModel.updateState(state);
    }

    @Override
    public void presentError(String errorMessage) {
        final AuthenticationState currentState = authenticationViewModel.getState();
        final AuthenticationState state = new AuthenticationState();
        state.setErrorMessage(errorMessage);
        // Preserve the current mode (sign-in vs sign-up)
        if (currentState != null) {
            state.setSignUpMode(currentState.isSignUpMode());
        }
        authenticationViewModel.updateState(state);
    }
}
