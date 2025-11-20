package app.use_cases.settings;

public class SettingsInteractor implements SettingsInputBoundary {
    private SettingsOutputBoundary settingsOutputBoundary;
    private SettingsDataAccessInterface settingsDataAccessInterface;

    public SettingsInteractor(SettingsOutputBoundary settingsOutputBoundary, SettingsDataAccessInterface settingsDataAccessInterface) {
        this.settingsOutputBoundary = settingsOutputBoundary;
        this.settingsDataAccessInterface = settingsDataAccessInterface;
    }

    @Override
    public void getSettings() {
        try {
            UserSettingsData userSettingsDto = settingsDataAccessInterface.getSettings();
            settingsOutputBoundary.getSettingsSuccess(userSettingsDto);
        } catch (Exception e) {
            settingsOutputBoundary.getSettingsFailure("Failed to fetch settings: " + e.getMessage());
        }
    }

    @Override
    public void updateUserSettings(UserSettingsData userSettingsDto) {
        try {
            settingsDataAccessInterface.saveSettings(userSettingsDto);
            settingsOutputBoundary.updateUserSettingsSuccess(userSettingsDto);
        } catch (Exception e) {
            settingsOutputBoundary.updateUserSettingsFailure("Failed to update settings: " + e.getMessage());
        }
    }
}
