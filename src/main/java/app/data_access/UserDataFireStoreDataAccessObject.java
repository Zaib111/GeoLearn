package app.data_access;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import app.entities.Country;
import app.entities.CountryCollection;
import app.entities.QuestionType;
import app.entities.QuizHistoryEntry;
import app.entities.QuizType;
import app.entities.User;
import app.use_cases.country_collection.CollectionDataAccessInterface;
import app.use_cases.quiz.QuizHistoryDataAccessInterface;
import app.use_cases.authentication.AuthenticationDataAccessInterface;
import app.use_cases.authentication.AuthenticationData;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Firestore implementation of user data access.
 * Uses Firestore REST API to persist collections and quiz history.
 * All data is organized under a user document identified by username.
 */
public class UserDataFireStoreDataAccessObject implements AuthenticationDataAccessInterface, CollectionDataAccessInterface,
        QuizHistoryDataAccessInterface {

    private static final String FIREBASE_BASE_URL =
            "https://firestore.googleapis.com/v1/projects/geolearn-aaf10/databases/(default)/documents/";
    private static final String USERS_COLLECTION = "users";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static final String FIELDS_KEY = "fields";
    private static final String COLLECTIONS_KEY = "collections";
    private static final String QUIZ_HISTORY_KEY = "quizHistory";
    private static final String ARRAY_VALUE_KEY = "arrayValue";
    private static final String VALUES_KEY = "values";
    private static final String MAP_VALUE_KEY = "mapValue";
    private static final String COLLECTION_ID_KEY = "collectionId";
    private static final String INTEGER_VALUE_KEY = "integerValue";
    private static final int HTTP_NOT_FOUND = 404;

    private final OkHttpClient httpClient;
    private User currentUser;
    private final List<CountryCollection> inMemoryCollections;
    private final List<QuizHistoryEntry> inMemoryQuizHistory;

    /**
     * Constructs a new UserDataFireStoreDataAccessObject with a default OkHttp client.
     */
    public UserDataFireStoreDataAccessObject() {
        this.httpClient = new OkHttpClient();
        this.currentUser = new User();
        this.inMemoryCollections = new ArrayList<>();
        this.inMemoryQuizHistory = new ArrayList<>();
    }

    private String getUsername() {
        return currentUser.getUsername();
    }

    private boolean shouldUseInMemory() {
        return getUsername() == null || getUsername().isBlank();
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
            if (response.code() == HTTP_NOT_FOUND || response.body() == null) {
                return createUserDocument();
            }

            final JSONObject doc = new JSONObject(response.body().string());
            return doc.getJSONObject(FIELDS_KEY);
        }
    }

    private JSONObject createUserDocument() throws IOException {
        final JSONObject fields = new JSONObject();
        fields.put("username", createStringValue(getUsername()));
        fields.put("password", createStringValue(currentUser.getPassword() != null ? currentUser.getPassword() : ""));
        fields.put(COLLECTIONS_KEY, createArrayValue(new JSONArray()));
        fields.put(QUIZ_HISTORY_KEY, createArrayValue(new JSONArray()));

        final JSONObject document = new JSONObject();
        document.put(FIELDS_KEY, fields);

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
        document.put(FIELDS_KEY, fields);

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
        if (shouldUseInMemory()) {
            inMemoryCollections.add(countryCollection);
        }
        else {
            createCollectionInFirestore(countryCollection);
        }
    }

    private void createCollectionInFirestore(CountryCollection countryCollection) {
        try {
            final JSONObject userFields = getUserDocument();
            final JSONArray collectionsArray = extractCollectionsArray(userFields);
            final JSONArray newCollections = createNewCollectionsArray(collectionsArray);

            newCollections.put(buildCollectionMap(countryCollection));
            userFields.put(COLLECTIONS_KEY, createArrayValue(newCollections));
            updateUserDocument(userFields);
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to create collection in Firestore", ex);
        }
    }

    private JSONArray extractCollectionsArray(JSONObject userFields) {
        JSONArray result = null;
        if (userFields.has(COLLECTIONS_KEY)) {
            result = userFields.getJSONObject(COLLECTIONS_KEY)
                    .getJSONObject(ARRAY_VALUE_KEY)
                    .optJSONArray(VALUES_KEY);
        }
        return result;
    }

    private JSONArray createNewCollectionsArray(JSONArray collectionsArray) {
        JSONArray result = new JSONArray();
        if (collectionsArray != null) {
            result = new JSONArray(collectionsArray.toString());
        }
        return result;
    }

    @Override
    public List<CountryCollection> getAllCollections() {
        if (shouldUseInMemory()) {
            return new ArrayList<>(inMemoryCollections);
        }

        return getAllCollectionsFromFirestore();
    }

    private List<CountryCollection> getAllCollectionsFromFirestore() {
        try {
            final JSONObject userFields = getUserDocument();
            final List<CountryCollection> collections = new ArrayList<>();

            if (userFields.has(COLLECTIONS_KEY)) {
                final JSONArray collectionsArray = userFields.getJSONObject(COLLECTIONS_KEY)
                        .getJSONObject(ARRAY_VALUE_KEY)
                        .optJSONArray(VALUES_KEY);

                if (collectionsArray != null) {
                    for (int i = 0; i < collectionsArray.length(); i++) {
                        final JSONObject collectionMap = collectionsArray.getJSONObject(i)
                                .getJSONObject(MAP_VALUE_KEY)
                                .getJSONObject(FIELDS_KEY);
                        collections.add(parseCollectionMap(collectionMap));
                    }
                }
            }

            return collections;
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to get collections from Firestore", ex);
        }
    }

    @Override
    public Optional<CountryCollection> getCollectionById(UUID collectionId) {
        if (shouldUseInMemory()) {
            return inMemoryCollections.stream()
                    .filter(collection -> collection.getCollectionId().equals(collectionId))
                    .findFirst();
        }

        return getAllCollections().stream()
                .filter(collection -> collection.getCollectionId().equals(collectionId))
                .findFirst();
    }

    @Override
    public void deleteCollection(UUID collectionId) {
        if (shouldUseInMemory()) {
            inMemoryCollections.removeIf(collection -> collection.getCollectionId().equals(collectionId));
        }
        else {
            deleteCollectionFromFirestore(collectionId);
        }
    }

    private void deleteCollectionFromFirestore(UUID collectionId) {
        try {
            final JSONObject userFields = getUserDocument();

            if (userFields.has(COLLECTIONS_KEY)) {
                final JSONArray collectionsArray = userFields.getJSONObject(COLLECTIONS_KEY)
                        .getJSONObject(ARRAY_VALUE_KEY)
                        .optJSONArray(VALUES_KEY);

                if (collectionsArray != null) {
                    final JSONArray newCollections = new JSONArray();

                    for (int i = 0; i < collectionsArray.length(); i++) {
                        final JSONObject collectionMap = collectionsArray.getJSONObject(i)
                                .getJSONObject(MAP_VALUE_KEY)
                                .getJSONObject(FIELDS_KEY);
                        final UUID currentId = UUID.fromString(
                                getStringValue(collectionMap.getJSONObject(COLLECTION_ID_KEY))
                        );

                        if (!currentId.equals(collectionId)) {
                            newCollections.put(collectionsArray.getJSONObject(i));
                        }
                    }

                    userFields.put(COLLECTIONS_KEY, createArrayValue(newCollections));
                    updateUserDocument(userFields);
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to delete collection from Firestore", ex);
        }
    }

    @Override
    public void updateCollection(CountryCollection updatedCollection) {
        if (shouldUseInMemory()) {
            updateInMemoryCollection(updatedCollection);
        }
        else {
            updateCollectionInFirestore(updatedCollection);
        }
    }

    private void updateCollectionInFirestore(CountryCollection updatedCollection) {
        try {
            final JSONObject userFields = getUserDocument();

            if (userFields.has(COLLECTIONS_KEY)) {
                final JSONArray collectionsArray = userFields.getJSONObject(COLLECTIONS_KEY)
                        .getJSONObject(ARRAY_VALUE_KEY)
                        .optJSONArray(VALUES_KEY);

                if (collectionsArray != null) {
                    final JSONArray newCollections = new JSONArray();

                    for (int i = 0; i < collectionsArray.length(); i++) {
                        final JSONObject collectionMap = collectionsArray.getJSONObject(i)
                                .getJSONObject(MAP_VALUE_KEY)
                                .getJSONObject(FIELDS_KEY);
                        final UUID currentId = UUID.fromString(
                                getStringValue(collectionMap.getJSONObject(COLLECTION_ID_KEY))
                        );

                        if (currentId.equals(updatedCollection.getCollectionId())) {
                            newCollections.put(buildCollectionMap(updatedCollection));
                        }
                        else {
                            newCollections.put(collectionsArray.getJSONObject(i));
                        }
                    }

                    userFields.put(COLLECTIONS_KEY, createArrayValue(newCollections));
                    updateUserDocument(userFields);
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to update collection in Firestore", ex);
        }
    }

    private void updateInMemoryCollection(CountryCollection updatedCollection) {
        for (int i = 0; i < inMemoryCollections.size(); i++) {
            if (inMemoryCollections.get(i).getCollectionId().equals(updatedCollection.getCollectionId())) {
                inMemoryCollections.set(i, updatedCollection);
                break;
            }
        }
    }

    @Override
    public AuthenticationData getUserAuth(AuthenticationData user) {
        if (user == null || user.getUsername() == null || user.getUsername().isBlank()) {
            return new AuthenticationData("", "");
        }

        try {
            final String url = FIREBASE_BASE_URL + USERS_COLLECTION + "/" + user.getUsername();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.code() == HTTP_NOT_FOUND || response.body() == null) {
                    return new AuthenticationData("", "");
                }

                final JSONObject doc = new JSONObject(response.body().string());
                final JSONObject fields = doc.getJSONObject(FIELDS_KEY);

                final String username = getStringValue(fields.getJSONObject("username"));
                final String password = fields.has("password") ? getStringValue(fields.getJSONObject("password")) : "";

                return new AuthenticationData(username, password);
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to get user auth from Firestore", ex);
        }
    }

    @Override
    public void setCurrentUser(AuthenticationData user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            this.currentUser = new User("", "");
            inMemoryCollections.clear();
            inMemoryQuizHistory.clear();
            return;
        }

        user.setUsername(user.getUsername().strip());
        this.currentUser = new User(user.getUsername(), user.getPassword());

        if (shouldUseInMemory()) {
            inMemoryCollections.clear();
            inMemoryQuizHistory.clear();
        }
    }

    @Override
    public void saveUserToDatabase(AuthenticationData user) {
        if (user == null || user.getUsername() == null) {
            return;
        }

        final String username = user.getUsername().strip();
        final String password = user.getPassword();

        try {
            final String url = FIREBASE_BASE_URL + USERS_COLLECTION + "/" + username;
            final Request checkRequest = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            boolean userExists = false;
            try (Response checkResponse = httpClient.newCall(checkRequest).execute()) {
                userExists = checkResponse.isSuccessful() && checkResponse.code() != HTTP_NOT_FOUND;
            }

            // Temporarily set current user to create/update the document
            final User previousUser = this.currentUser;
            this.currentUser = new User(username, password);

            if (userExists) {
                // User exists - update password
                final JSONObject userFields = getUserDocument();
                userFields.put("password", createStringValue(password != null ? password : ""));
                updateUserDocument(userFields);
            }
            else {
                // User doesn't exist - create new user document
                createUserDocument();
            }

            // Restore previous current user
            this.currentUser = previousUser;
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to save user to Firestore", ex);
        }
    }

    @Override
    public synchronized void saveQuizAttempt(QuizHistoryEntry entry) {
        if (shouldUseInMemory()) {
            inMemoryQuizHistory.add(entry);
        }
        else {
            saveQuizAttemptToFirestore(entry);
        }
    }

    private void saveQuizAttemptToFirestore(QuizHistoryEntry entry) {
        try {
            final JSONObject userFields = getUserDocument();
            final JSONArray historyArray = extractQuizHistoryArray(userFields);
            final JSONArray newHistory = createNewHistoryArray(historyArray);

            newHistory.put(buildQuizHistoryMap(entry));
            userFields.put(QUIZ_HISTORY_KEY, createArrayValue(newHistory));
            updateUserDocument(userFields);
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to save quiz attempt to Firestore", ex);
        }
    }

    private JSONArray extractQuizHistoryArray(JSONObject userFields) {
        JSONArray result = null;
        if (userFields.has(QUIZ_HISTORY_KEY)) {
            result = userFields.getJSONObject(QUIZ_HISTORY_KEY)
                    .getJSONObject(ARRAY_VALUE_KEY)
                    .optJSONArray(VALUES_KEY);
        }
        return result;
    }

    private JSONArray createNewHistoryArray(JSONArray historyArray) {
        JSONArray result = new JSONArray();
        if (historyArray != null) {
            result = new JSONArray(historyArray.toString());
        }
        return result;
    }

    @Override
    public synchronized List<QuizHistoryEntry> getAllQuizAttempts() {
        if (shouldUseInMemory()) {
            return new ArrayList<>(inMemoryQuizHistory);
        }

        return getAllQuizAttemptsFromFirestore();
    }

    private List<QuizHistoryEntry> getAllQuizAttemptsFromFirestore() {
        try {
            final JSONObject userFields = getUserDocument();
            final List<QuizHistoryEntry> history = new ArrayList<>();

            if (userFields.has(QUIZ_HISTORY_KEY)) {
                final JSONArray historyArray = userFields.getJSONObject(QUIZ_HISTORY_KEY)
                        .getJSONObject(ARRAY_VALUE_KEY)
                        .optJSONArray(VALUES_KEY);

                if (historyArray != null) {
                    for (int i = 0; i < historyArray.length(); i++) {
                        final JSONObject historyMap = historyArray.getJSONObject(i)
                                .getJSONObject(MAP_VALUE_KEY)
                                .getJSONObject(FIELDS_KEY);
                        history.add(parseQuizHistoryMap(historyMap));
                    }
                }
            }

            return history;
        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to get quiz attempts from Firestore", ex);
        }
    }

    private JSONObject buildCollectionMap(CountryCollection collection) {
        final JSONObject fields = new JSONObject();
        fields.put(COLLECTION_ID_KEY, createStringValue(collection.getCollectionId().toString()));
        fields.put("collectionName", createStringValue(collection.getCollectionName()));

        final JSONArray countriesArray = new JSONArray();
        for (Country country : collection.getCountries()) {
            countriesArray.put(buildCountryMap(country));
        }
        fields.put("countries", createArrayValue(countriesArray));

        return new JSONObject().put(MAP_VALUE_KEY, new JSONObject().put(FIELDS_KEY, fields));
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

        return new JSONObject().put(MAP_VALUE_KEY, new JSONObject().put(FIELDS_KEY, fields));
    }

    private JSONObject buildQuizHistoryMap(QuizHistoryEntry entry) {
        final JSONObject fields = new JSONObject();
        fields.put("quizType", createStringValue(entry.getQuizType().name()));
        fields.put("questionType", createStringValue(entry.getQuestionType().name()));
        fields.put("numQuestions", createIntegerValue(entry.getNumQuestions()));
        fields.put("score", createIntegerValue(entry.getScore()));
        fields.put("durationSeconds", createIntegerValue(entry.getDurationSeconds()));
        fields.put("highestStreak", createIntegerValue(entry.getHighestStreak()));
        fields.put("completedAt",
                createStringValue(entry.getCompletedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));

        return new JSONObject().put(MAP_VALUE_KEY, new JSONObject().put(FIELDS_KEY, fields));
    }

    private CountryCollection parseCollectionMap(JSONObject fields) {
        final UUID collectionId = UUID.fromString(getStringValue(fields.getJSONObject(COLLECTION_ID_KEY)));
        final String collectionName = getStringValue(fields.getJSONObject("collectionName"));

        final List<Country> countries = new ArrayList<>();
        final JSONArray countriesArray = fields.getJSONObject("countries")
                .getJSONObject(ARRAY_VALUE_KEY)
                .optJSONArray(VALUES_KEY);

        if (countriesArray != null) {
            for (int i = 0; i < countriesArray.length(); i++) {
                final JSONObject countryMap = countriesArray.getJSONObject(i)
                        .getJSONObject(MAP_VALUE_KEY)
                        .getJSONObject(FIELDS_KEY);
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

        final String capitalValue = getOptionalString(capital);
        final String subregionValue = getOptionalString(subregion);

        return new Country(
                code, name,
                capitalValue,
                region,
                subregionValue,
                population, areaKm2, borders, flagUrl, languages, currencies, timezones
        );
    }

    private String getOptionalString(String value) {
        String result = null;
        if (!value.isEmpty()) {
            result = value;
        }
        return result;
    }

    private JSONObject createStringValue(String value) {
        return new JSONObject().put("stringValue", value);
    }

    private JSONObject createIntegerValue(long value) {
        return new JSONObject().put(INTEGER_VALUE_KEY, String.valueOf(value));
    }

    private JSONObject createDoubleValue(double value) {
        return new JSONObject().put("doubleValue", value);
    }

    private JSONObject createArrayValue(JSONArray array) {
        return new JSONObject().put(ARRAY_VALUE_KEY, new JSONObject().put(VALUES_KEY, array));
    }

    private String getStringValue(JSONObject field) {
        return field.optString("stringValue", "");
    }

    private int getIntValue(JSONObject field) {
        final String intStr = field.optString(INTEGER_VALUE_KEY, "0");
        return Integer.parseInt(intStr);
    }

    private long getLongValue(JSONObject field) {
        final String longStr = field.optString(INTEGER_VALUE_KEY, "0");
        return Long.parseLong(longStr);
    }

    private double getDoubleValue(JSONObject field) {
        return field.optDouble("doubleValue", 0.0);
    }

    private List<String> getStringList(JSONObject field) {
        final List<String> result = new ArrayList<>();
        final JSONObject arrayValueObj = field.optJSONObject(ARRAY_VALUE_KEY);
        JSONArray array = null;
        if (arrayValueObj != null) {
            array = arrayValueObj.optJSONArray(VALUES_KEY);
        }

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
