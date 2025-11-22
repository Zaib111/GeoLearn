package app.use_cases.settings;

public interface SettingsOutputBoundary {
    void getSettingsSuccess(UserSettingsData userSettingsDto);
    void getSettingsFailure(String errorMessage);
    void updateUserSettingsSuccess(UserSettingsData userSettingsDto);
    void updateUserSettingsFailure(String errorMessage);
}
