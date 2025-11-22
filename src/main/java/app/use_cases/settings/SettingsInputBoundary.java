package app.use_cases.settings;

public interface SettingsInputBoundary {
    void getSettings();
    void updateUserSettings(UserSettingsData userSettingsDto);
}
