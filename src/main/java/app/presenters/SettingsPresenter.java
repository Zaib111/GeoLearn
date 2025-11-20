package app.presenters;

import app.use_cases.settings.SettingsOutputBoundary;
import app.use_cases.settings.UserSettingsData;
import app.views.ViewModel;
import app.views.settings.SettingsState;

public class SettingsPresenter implements SettingsOutputBoundary {
    private final ViewModel<SettingsState> settingsViewModel;

    public SettingsPresenter(ViewModel<SettingsState> settingsViewModel) {
        this.settingsViewModel = settingsViewModel;
    }

    @Override
    public void getSettingsSuccess(UserSettingsData userSettingsDto) {
        SettingsState state = new SettingsState();
        state.setUsername(userSettingsDto.getUsername());
        state.setSuccessMessage(null);
        state.setErrorMessage(null);
        settingsViewModel.updateState(state);
    }

    @Override
    public void getSettingsFailure(String errorMessage) {
        SettingsState state = new SettingsState();
        state.setUsername("");
        state.setSuccessMessage(null);
        state.setErrorMessage(errorMessage);
        settingsViewModel.updateState(state);
    }

    @Override
    public void updateUserSettingsSuccess(UserSettingsData userSettingsDto) {
        SettingsState state = new SettingsState();
        state.setUsername(userSettingsDto.getUsername());
        state.setSuccessMessage("Settings saved successfully!");
        state.setErrorMessage(null);
        settingsViewModel.updateState(state);
    }

    @Override
    public void updateUserSettingsFailure(String errorMessage) {
        SettingsState state = settingsViewModel.getState();
        state.setSuccessMessage(null);
        state.setErrorMessage("Failed to save settings: " + errorMessage);
        settingsViewModel.updateState(state);
    }
}
