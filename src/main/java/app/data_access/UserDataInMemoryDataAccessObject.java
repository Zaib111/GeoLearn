package app.data_access;

import app.entities.CountryCollection;
import app.use_cases.collection.CollectionDataAccessInterface;
import app.use_cases.settings.SettingsDataAccessInterface;
import app.use_cases.settings.UserSettingsData;
import app.entities.QuizHistoryEntry;
import app.use_cases.quiz.QuizHistoryDataAccessInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDataInMemoryDataAccessObject implements SettingsDataAccessInterface, CollectionDataAccessInterface, QuizHistoryDataAccessInterface {
    UserSettingsData currentSettings = new UserSettingsData();
    List<CountryCollection> collections = new ArrayList<>();
    private final List<QuizHistoryEntry> quizHistory = new ArrayList<>();

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

    @Override
    public synchronized void saveQuizAttempt(QuizHistoryEntry entry) {
        quizHistory.add(entry);
    }

    @Override
    public synchronized List<QuizHistoryEntry> getAllQuizAttempts() {
        return new ArrayList<>(quizHistory);
    }

}
