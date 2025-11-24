package app.use_cases.settings;

/**
 * Interactor for settings use case.
 */
public class SettingsInteractor implements SettingsInputBoundary {
    private final SettingsOutputBoundary settingsOutputBoundary;
    private final SettingsDataAccessInterface settingsDataAccessInterface;

    /**
     * Constructs a SettingsInteractor.
     *
     * @param settingsOutputBoundary the output boundary for settings
     * @param settingsDataAccessInterface the data access interface for settings
     */
    public SettingsInteractor(SettingsOutputBoundary settingsOutputBoundary,
                              SettingsDataAccessInterface settingsDataAccessInterface) {
        this.settingsOutputBoundary = settingsOutputBoundary;
        this.settingsDataAccessInterface = settingsDataAccessInterface;
    }

    @Override
    public void getSettings() {
        final UserSettingsData userSettingsDto =
                settingsDataAccessInterface.getSettings();
        settingsOutputBoundary.getSettingsSuccess(userSettingsDto);
    }

    @Override
    public void updateUserSettings(UserSettingsData userSettingsDto) {
        settingsDataAccessInterface.saveSettings(userSettingsDto);
        settingsOutputBoundary.updateUserSettingsSuccess(userSettingsDto);
    }
}
