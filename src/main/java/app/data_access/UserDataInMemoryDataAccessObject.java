package app.data_access;

import app.entities.CountryCollection;
import app.use_cases.collection.CollectionUserDataAccessInterface;
import app.use_cases.settings.SettingsDataAccessInterface;
import app.use_cases.settings.UserSettingsData;

import java.util.ArrayList;
import java.util.List;

public class UserDataInMemoryDataAccessObject implements SettingsDataAccessInterface, CollectionUserDataAccessInterface {
    UserSettingsData currentSettings = new UserSettingsData();
    List<CountryCollection> collections = new ArrayList<>();

    @Override
    public UserSettingsData getSettings() {
        return currentSettings;
    }

    @Override
    public void saveSettings(UserSettingsData userSettingsDto) {
        currentSettings = userSettingsDto;
    }

    @Override
    public void createCollection(CountryCollection countryCollection) {
        collections.add(countryCollection);
    }

    @Override
    public List<CountryCollection> getAllCollections() {
        return new ArrayList<>(collections);
    }
}
