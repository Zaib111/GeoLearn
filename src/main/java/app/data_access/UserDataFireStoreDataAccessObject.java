package app.data_access;

import app.entities.Country;
import app.entities.CountryCollection;
import app.entities.QuestionType;
import app.entities.QuizHistoryEntry;
import app.entities.QuizType;
import app.use_cases.country_collection.CollectionDataAccessInterface;
import app.use_cases.quiz.QuizHistoryDataAccessInterface;
import app.use_cases.settings.SettingsDataAccessInterface;
import app.use_cases.settings.UserSettingsData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Firestore implementation of user data access.
 * Uses Firestore REST API to persist collections and quiz history.
 * All data is organized under a user document identified by username.
 */
public class UserDataFireStoreDataAccessObject implements SettingsDataAccessInterface, CollectionDataAccessInterface,
        QuizHistoryDataAccessInterface {

    private static final String FIREBASE_BASE_URL =
            "https://firestore.googleapis.com/v1/projects/geolearn-aaf10/databases/(default)/documents/";
    private static final String USERS_COLLECTION = "users";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient httpClient;
    private UserSettingsData currentSettings;
    private List<CountryCollection> inMemoryCollections;
    private List<QuizHistoryEntry> inMemoryQuizHistory;

    /**
     * Constructs a new UserDataFireStoreDataAccessObject with a default OkHttp client.
     */
    public UserDataFireStoreDataAccessObject() {
        this.httpClient = new OkHttpClient();
        this.currentSettings = new UserSettingsData();
        this.inMemoryCollections = new ArrayList<>();
        this.inMemoryQuizHistory = new ArrayList<>();
    }

    private String getUsername() {
        return currentSettings.getUsername();
    }

    private boolean shouldUseFirestore() {
        return getUsername() != null && !getUsername().isBlank();
    }

    private String getUserDocumentUrl() {
        return FIREBASE_BASE_URL + USERS_COLLECTION + "/" + getUsername();
    }

    private JSONObject getUserDocument() throws IOException {
        final String url = getUserDocumentUrl();
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.code() == 404 || response.body() == null) {
                return createUserDocument();
            }

            final JSONObject doc = new JSONObject(response.body().string());
            return doc.getJSONObject("fields");
        }
    }

    private JSONObject createUserDocument() throws IOException {
        final JSONObject fields = new JSONObject();
        fields.put("username", createStringValue(getUsername()));
        fields.put("collections", createArrayValue(new JSONArray()));
        fields.put("quizHistory", createArrayValue(new JSONArray()));

        final JSONObject document = new JSONObject();
        document.put("fields", fields);

        final String url = FIREBASE_BASE_URL + USERS_COLLECTION + "?documentId=" + getUsername();
        final RequestBody body = RequestBody.create(document.toString(), JSON);
        final Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to create user document: " + response.code());
            }
        }

        return fields;
    }

    private void updateUserDocument(JSONObject fields) throws IOException {
        final JSONObject document = new JSONObject();
        document.put("fields", fields);

        final String url = getUserDocumentUrl();
        final RequestBody body = RequestBody.create(document.toString(), JSON);
        final Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to update user document: " + response.code());
            }
        }
    }

    @Override
    public void createCollection(CountryCollection countryCollection) {
        if (!shouldUseFirestore()) {
            inMemoryCollections.add(countryCollection);
            return;
        }

        try {
            final JSONObject userFields = getUserDocument();
            final JSONArray collectionsArray = userFields.has("collections")
                    ? userFields.getJSONObject("collections").getJSONObject("arrayValue").optJSONArray("values")
                    : null;
            final JSONArray newCollections = collectionsArray != null
                    ? new JSONArray(collectionsArray.toString())
                    : new JSONArray();

            newCollections.put(buildCollectionMap(countryCollection));
            userFields.put("collections", createArrayValue(newCollections));
            updateUserDocument(userFields);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to create collection in Firestore", ex);
        }
    }

    @Override
    public List<CountryCollection> getAllCollections() {
        if (!shouldUseFirestore()) {
            return new ArrayList<>(inMemoryCollections);
        }

        try {
            final JSONObject userFields = getUserDocument();
            final List<CountryCollection> collections = new ArrayList<>();

            if (userFields.has("collections")) {
                final JSONArray collectionsArray = userFields.getJSONObject("collections")
                        .getJSONObject("arrayValue")
                        .optJSONArray("values");

                if (collectionsArray != null) {
                    for (int i = 0; i < collectionsArray.length(); i++) {
                        final JSONObject collectionMap = collectionsArray.getJSONObject(i)
                                .getJSONObject("mapValue")
                                .getJSONObject("fields");
                        collections.add(parseCollectionMap(collectionMap));
                    }
                }
            }

            return collections;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get collections from Firestore", ex);
        }
    }

    @Override
    public Optional<CountryCollection> getCollectionById(UUID collectionId) {
        if (!shouldUseFirestore()) {
            return inMemoryCollections.stream()
                    .filter(c -> c.getCollectionId().equals(collectionId))
                    .findFirst();
        }

        return getAllCollections().stream()
                .filter(c -> c.getCollectionId().equals(collectionId))
                .findFirst();
    }

    @Override
    public void deleteCollection(UUID collectionId) {
        if (!shouldUseFirestore()) {
            inMemoryCollections.removeIf(c -> c.getCollectionId().equals(collectionId));
            return;
        }

        try {
            final JSONObject userFields = getUserDocument();

            if (userFields.has("collections")) {
                final JSONArray collectionsArray = userFields.getJSONObject("collections")
                        .getJSONObject("arrayValue")
                        .optJSONArray("values");

                if (collectionsArray != null) {
                    final JSONArray newCollections = new JSONArray();

                    for (int i = 0; i < collectionsArray.length(); i++) {
                        final JSONObject collectionMap = collectionsArray.getJSONObject(i)
                                .getJSONObject("mapValue")
                                .getJSONObject("fields");
                        final UUID currentId = UUID.fromString(
                                getStringValue(collectionMap.getJSONObject("collectionId"))
                        );

                        if (!currentId.equals(collectionId)) {
                            newCollections.put(collectionsArray.getJSONObject(i));
                        }
                    }

                    userFields.put("collections", createArrayValue(newCollections));
                    updateUserDocument(userFields);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete collection from Firestore", ex);
        }
    }

    @Override
    public void updateCollection(CountryCollection updatedCollection) {
        if (!shouldUseFirestore()) {
            for (int i = 0; i < inMemoryCollections.size(); i++) {
                if (inMemoryCollections.get(i).getCollectionId().equals(updatedCollection.getCollectionId())) {
                    inMemoryCollections.set(i, updatedCollection);
                    return;
                }
            }
            return;
        }

        try {
            final JSONObject userFields = getUserDocument();

            if (userFields.has("collections")) {
                final JSONArray collectionsArray = userFields.getJSONObject("collections")
                        .getJSONObject("arrayValue")
                        .optJSONArray("values");

                if (collectionsArray != null) {
                    final JSONArray newCollections = new JSONArray();

                    for (int i = 0; i < collectionsArray.length(); i++) {
                        final JSONObject collectionMap = collectionsArray.getJSONObject(i)
                                .getJSONObject("mapValue")
                                .getJSONObject("fields");
                        final UUID currentId = UUID.fromString(
                                getStringValue(collectionMap.getJSONObject("collectionId"))
                        );

                        if (currentId.equals(updatedCollection.getCollectionId())) {
                            newCollections.put(buildCollectionMap(updatedCollection));
                        } else {
                            newCollections.put(collectionsArray.getJSONObject(i));
                        }
                    }

                    userFields.put("collections", createArrayValue(newCollections));
                    updateUserDocument(userFields);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to update collection in Firestore", ex);
        }
    }

    @Override
    public UserSettingsData getSettings() {
        return currentSettings;
    }

    @Override
    public void saveSettings(UserSettingsData userSettingsDto) {
        userSettingsDto.setUsername(userSettingsDto.getUsername().strip());
        this.currentSettings = userSettingsDto;
        if (!shouldUseFirestore()) {
            inMemoryCollections.clear();
            inMemoryQuizHistory.clear();
        }
    }

    @Override
    public synchronized void saveQuizAttempt(QuizHistoryEntry entry) {
        if (!shouldUseFirestore()) {
            inMemoryQuizHistory.add(entry);
            return;
        }

        try {
            final JSONObject userFields = getUserDocument();
            final JSONArray historyArray = userFields.has("quizHistory")
                    ? userFields.getJSONObject("quizHistory").getJSONObject("arrayValue").optJSONArray("values")
                    : null;
            final JSONArray newHistory = historyArray != null
                    ? new JSONArray(historyArray.toString())
                    : new JSONArray();

            newHistory.put(buildQuizHistoryMap(entry));
            userFields.put("quizHistory", createArrayValue(newHistory));
            updateUserDocument(userFields);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to save quiz attempt to Firestore", ex);
        }
    }

    @Override
    public synchronized List<QuizHistoryEntry> getAllQuizAttempts() {
        if (!shouldUseFirestore()) {
            return new ArrayList<>(inMemoryQuizHistory);
        }

        try {
            final JSONObject userFields = getUserDocument();
            final List<QuizHistoryEntry> history = new ArrayList<>();

            if (userFields.has("quizHistory")) {
                final JSONArray historyArray = userFields.getJSONObject("quizHistory")
                        .getJSONObject("arrayValue")
                        .optJSONArray("values");

                if (historyArray != null) {
                    for (int i = 0; i < historyArray.length(); i++) {
                        final JSONObject historyMap = historyArray.getJSONObject(i)
                                .getJSONObject("mapValue")
                                .getJSONObject("fields");
                        history.add(parseQuizHistoryMap(historyMap));
                    }
                }
            }

            return history;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to get quiz attempts from Firestore", ex);
        }
    }

    private JSONObject buildCollectionMap(CountryCollection collection) {
        final JSONObject fields = new JSONObject();
        fields.put("collectionId", createStringValue(collection.getCollectionId().toString()));
        fields.put("collectionName", createStringValue(collection.getCollectionName()));

        final JSONArray countriesArray = new JSONArray();
        for (Country country : collection.getCountries()) {
            countriesArray.put(buildCountryMap(country));
        }
        fields.put("countries", createArrayValue(countriesArray));

        return new JSONObject().put("mapValue", new JSONObject().put("fields", fields));
    }

    private JSONObject buildCountryMap(Country country) {
        final JSONObject fields = new JSONObject();
        fields.put("code", createStringValue(country.getCode()));
        fields.put("name", createStringValue(country.getName()));
        fields.put("capital", createStringValue(country.getCapital().orElse("")));
        fields.put("region", createStringValue(country.getRegion()));
        fields.put("subregion", createStringValue(country.getSubregion().orElse("")));
        fields.put("population", createIntegerValue(country.getPopulation()));
        fields.put("areaKm2", createDoubleValue(country.getAreaKm2()));
        fields.put("borders", createArrayValue(stringListToJsonArray(country.getBorders())));
        fields.put("flagUrl", createStringValue(country.getFlagUrl()));
        fields.put("languages", createArrayValue(stringListToJsonArray(country.getLanguages())));
        fields.put("currencies", createArrayValue(stringListToJsonArray(country.getCurrencies())));
        fields.put("timezones", createArrayValue(stringListToJsonArray(country.getTimezones())));

        return new JSONObject().put("mapValue", new JSONObject().put("fields", fields));
    }

    private JSONObject buildQuizHistoryMap(QuizHistoryEntry entry) {
        final JSONObject fields = new JSONObject();
        fields.put("quizType", createStringValue(entry.getQuizType().name()));
        fields.put("questionType", createStringValue(entry.getQuestionType().name()));
        fields.put("numQuestions", createIntegerValue(entry.getNumQuestions()));
        fields.put("score", createIntegerValue(entry.getScore()));
        fields.put("durationSeconds", createIntegerValue(entry.getDurationSeconds()));
        fields.put("highestStreak", createIntegerValue(entry.getHighestStreak()));
        fields.put("completedAt", createStringValue(entry.getCompletedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        return new JSONObject().put("mapValue", new JSONObject().put("fields", fields));
    }

    private CountryCollection parseCollectionMap(JSONObject fields) {
        final UUID collectionId = UUID.fromString(getStringValue(fields.getJSONObject("collectionId")));
        final String collectionName = getStringValue(fields.getJSONObject("collectionName"));

        final List<Country> countries = new ArrayList<>();
        final JSONArray countriesArray = fields.getJSONObject("countries")
                .getJSONObject("arrayValue")
                .optJSONArray("values");

        if (countriesArray != null) {
            for (int i = 0; i < countriesArray.length(); i++) {
                final JSONObject countryMap = countriesArray.getJSONObject(i)
                        .getJSONObject("mapValue")
                        .getJSONObject("fields");
                countries.add(parseCountryMap(countryMap));
            }
        }

        return new CountryCollection(collectionId, collectionName, countries);
    }

    private QuizHistoryEntry parseQuizHistoryMap(JSONObject fields) {
        final QuizType quizType = QuizType.valueOf(getStringValue(fields.getJSONObject("quizType")));
        final QuestionType questionType = QuestionType.valueOf(getStringValue(fields.getJSONObject("questionType")));
        final int numQuestions = getIntValue(fields.getJSONObject("numQuestions"));
        final int score = getIntValue(fields.getJSONObject("score"));
        final int durationSeconds = getIntValue(fields.getJSONObject("durationSeconds"));
        final int highestStreak = getIntValue(fields.getJSONObject("highestStreak"));
        final LocalDateTime completedAt = LocalDateTime.parse(
                getStringValue(fields.getJSONObject("completedAt")),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        return new QuizHistoryEntry(quizType, questionType, numQuestions, score,
                durationSeconds, highestStreak, completedAt);
    }

    private Country parseCountryMap(JSONObject fields) {
        final String code = getStringValue(fields.getJSONObject("code"));
        final String name = getStringValue(fields.getJSONObject("name"));
        final String capital = getStringValue(fields.getJSONObject("capital"));
        final String region = getStringValue(fields.getJSONObject("region"));
        final String subregion = getStringValue(fields.getJSONObject("subregion"));
        final long population = getLongValue(fields.getJSONObject("population"));
        final double areaKm2 = getDoubleValue(fields.getJSONObject("areaKm2"));
        final List<String> borders = getStringList(fields.getJSONObject("borders"));
        final String flagUrl = getStringValue(fields.getJSONObject("flagUrl"));
        final List<String> languages = getStringList(fields.getJSONObject("languages"));
        final List<String> currencies = getStringList(fields.getJSONObject("currencies"));
        final List<String> timezones = getStringList(fields.getJSONObject("timezones"));

        return new Country(
                code, name,
                capital.isEmpty() ? null : capital,
                region,
                subregion.isEmpty() ? null : subregion,
                population, areaKm2, borders, flagUrl, languages, currencies, timezones
        );
    }

    private JSONObject createStringValue(String value) {
        return new JSONObject().put("stringValue", value);
    }

    private JSONObject createIntegerValue(long value) {
        return new JSONObject().put("integerValue", String.valueOf(value));
    }

    private JSONObject createDoubleValue(double value) {
        return new JSONObject().put("doubleValue", value);
    }

    private JSONObject createArrayValue(JSONArray array) {
        return new JSONObject().put("arrayValue", new JSONObject().put("values", array));
    }

    private String getStringValue(JSONObject field) {
        return field.optString("stringValue", "");
    }

    private int getIntValue(JSONObject field) {
        final String intStr = field.optString("integerValue", "0");
        return Integer.parseInt(intStr);
    }

    private long getLongValue(JSONObject field) {
        final String longStr = field.optString("integerValue", "0");
        return Long.parseLong(longStr);
    }

    private double getDoubleValue(JSONObject field) {
        return field.optDouble("doubleValue", 0.0);
    }

    private List<String> getStringList(JSONObject field) {
        final List<String> result = new ArrayList<>();
        final JSONArray array = field.optJSONObject("arrayValue") != null
                ? field.getJSONObject("arrayValue").optJSONArray("values")
                : null;

        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                final JSONObject item = array.getJSONObject(i);
                result.add(getStringValue(item));
            }
        }

        return result;
    }

    private JSONArray stringListToJsonArray(List<String> list) {
        final JSONArray array = new JSONArray();
        for (String item : list) {
            array.put(createStringValue(item));
        }
        return array;
    }
}
