package app.data_access;

import app.entities.CountryCollection;
import app.use_cases.collection.CollectionDataAccessInterface;
import app.use_cases.settings.SettingsDataAccessInterface;
import app.use_cases.settings.UserSettingsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDataInMemoryDataAccessObject implements SettingsDataAccessInterface, CollectionDataAccessInterface {
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

    @Override
    public Optional<CountryCollection> getCollectionById(UUID collectionId) {
        return collections.stream()
                .filter(c -> c.getCollectionId().equals(collectionId))
                .findFirst();
    }

    @Override
    public void deleteCollection(UUID collectionId) {
        collections.removeIf(c -> c.getCollectionId().equals(collectionId));
    }

    @Override
    public void updateCollection(CountryCollection updatedCollection) {
        for (int i = 0; i < collections.size(); i++) {
            if (collections.get(i).getCollectionId().equals(updatedCollection.getCollectionId())) {
                collections.set(i, updatedCollection);
                return;
            }
        }
    }
}
