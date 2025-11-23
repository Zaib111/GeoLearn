package app.data_access;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.entities.Country;
import app.use_cases.compare.CompareDataAccessInterface;
import app.use_cases.country.CountryDataAccessInterface;
import app.use_cases.detail.DetailDataAccessInterface;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APICountryDataAccessObject implements CountryDataAccessInterface, CompareDataAccessInterface, DetailDataAccessInterface {
    private static final String FIELD_NAME = "name";
    private static final String FIELD_CAPITAL = "capital";
    private static final String FIELD_REGION = "region";
    private static final String FIELD_SUBREGION = "subregion";
    private static final String FIELD_POPULATION = "population";
    private static final String FIELD_AREA = "area";
    private static final String FIELD_BORDERS = "borders";
    private static final String FIELD_FLAGS = "flags";
    private static final String FIELD_LANGUAGES = "languages";
    private static final String FIELD_CURRENCIES = "currencies";
    private static final String FIELD_TIMEZONES = "timezones";

    private final OkHttpClient client;
    private final String apiBase;

    public APICountryDataAccessObject() {
        this.client = new OkHttpClient().newBuilder().build();
        this.apiBase = "https://restcountries.com/v3.1/all?fields=cca3,";
    }

    @Override
    public List<Country> getCountries() {
        final List<Country> countries = new ArrayList<>();
        final CountryDataMaps dataMaps = new CountryDataMaps();

        final List<String> fieldGroups = List.of(
                FIELD_NAME,
                FIELD_CAPITAL,
                FIELD_REGION,
                FIELD_SUBREGION,
                FIELD_POPULATION,
                FIELD_AREA,
                FIELD_BORDERS,
                FIELD_FLAGS,
                FIELD_LANGUAGES,
                FIELD_CURRENCIES,
                FIELD_TIMEZONES
        );

        fieldGroups.forEach(field -> {
            fetchFieldData(field, dataMaps);
        });

        dataMaps.countryCodes.forEach(countryCode -> {
            final Country country = createCountry(countryCode, dataMaps);
            countries.add(country);
        });
        return countries;
    }

    private void fetchFieldData(String field, CountryDataMaps dataMaps) {
        final String url = apiBase.concat(field);
        final Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        try {
            final Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            final String responseBody = Objects.requireNonNull(response.body()).string();
            final JSONArray countryArray = new JSONArray(responseBody);

            processCountryArray(countryArray, field, dataMaps);
        }
        catch (IOException | JSONException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void processCountryArray(JSONArray countryArray, String field, CountryDataMaps dataMaps) {
        for (int i = 0; i < countryArray.length(); i++) {
            final JSONObject countryJson = countryArray.getJSONObject(i);
            final String code = countryJson.getString("cca3");

            dataMaps.countryCodes.add(code);

            processFieldData(field, code, countryJson, dataMaps);
        }
    }

    private void processFieldData(String field, String code, JSONObject countryJson, CountryDataMaps dataMaps) {
        switch (field) {
            case FIELD_NAME:
                processNameField(countryJson, code, dataMaps.nameMap);
                break;
            case FIELD_CAPITAL:
                processCapitalField(countryJson, code, dataMaps.capitalMap);
                break;
            case FIELD_REGION:
                processRegionField(countryJson, code, dataMaps.regionMap);
                break;
            case FIELD_SUBREGION:
                processSubregionField(countryJson, code, dataMaps.subregionMap);
                break;
            case FIELD_POPULATION:
                processPopulationField(countryJson, code, dataMaps.populationMap);
                break;
            case FIELD_AREA:
                processAreaField(countryJson, code, dataMaps.areaMap);
                break;
            case FIELD_BORDERS:
                processBordersField(countryJson, code, dataMaps.bordersMap);
                break;
            case FIELD_FLAGS:
                processFlagsField(countryJson, code, dataMaps.flagsMap);
                break;
            case FIELD_LANGUAGES:
                processLanguagesField(countryJson, code, dataMaps.languagesMap);
                break;
            case FIELD_CURRENCIES:
                processCurrenciesField(countryJson, code, dataMaps.currenciesMap);
                break;
            case FIELD_TIMEZONES:
                processTimezonesField(countryJson, code, dataMaps.timezonesMap);
                break;
            default:
                break;
        }
    }

    private void processNameField(JSONObject countryJson, String code, Map<String, String> nameMap) {
        final JSONObject nameObj = countryJson.getJSONObject(FIELD_NAME);
        final String commonName = nameObj.getString("common");
        nameMap.put(code, commonName);
    }

    private void processCapitalField(JSONObject countryJson, String code, Map<String, String> capitalMap) {
        if (countryJson.has(FIELD_CAPITAL)) {
            final JSONArray capitalArray = countryJson.getJSONArray(FIELD_CAPITAL);
            if (!capitalArray.isEmpty()) {
                capitalMap.put(code, capitalArray.getString(0));
            }
        }
    }

    private void processRegionField(JSONObject countryJson, String code, Map<String, String> regionMap) {
        final String region = countryJson.getString(FIELD_REGION);
        regionMap.put(code, region);
    }

    private void processSubregionField(JSONObject countryJson, String code, Map<String, String> subregionMap) {
        if (countryJson.has(FIELD_SUBREGION)) {
            final String subregion = countryJson.getString(FIELD_SUBREGION);
            subregionMap.put(code, subregion);
        }
    }

    private void processPopulationField(JSONObject countryJson, String code, Map<String, Integer> populationMap) {
        final int population = countryJson.getInt(FIELD_POPULATION);
        populationMap.put(code, population);
    }

    private void processAreaField(JSONObject countryJson, String code, Map<String, Double> areaMap) {
        final double area = countryJson.getDouble(FIELD_AREA);
        areaMap.put(code, area);
    }

    private void processBordersField(JSONObject countryJson, String code, Map<String, List<String>> bordersMap) {
        final List<String> borders = new ArrayList<>();
        if (countryJson.has(FIELD_BORDERS)) {
            final JSONArray bordersArray = countryJson.getJSONArray(FIELD_BORDERS);
            for (int j = 0; j < bordersArray.length(); j++) {
                borders.add(bordersArray.getString(j));
            }
        }
        bordersMap.put(code, borders);
    }

    private void processFlagsField(JSONObject countryJson, String code, Map<String, String> flagsMap) {
        final JSONObject flagsObj = countryJson.getJSONObject(FIELD_FLAGS);
        final String flagUrl = flagsObj.getString("png");
        flagsMap.put(code, flagUrl);
    }

    private void processLanguagesField(JSONObject countryJson, String code, Map<String, List<String>> languagesMap) {
        final List<String> languages = new ArrayList<>();
        if (countryJson.has(FIELD_LANGUAGES)) {
            final JSONObject languagesObj = countryJson.getJSONObject(FIELD_LANGUAGES);
            final Iterator<String> keys = languagesObj.keys();
            while (keys.hasNext()) {
                final String key = keys.next();
                languages.add(languagesObj.getString(key));
            }
        }
        languagesMap.put(code, languages);
    }

    private void processCurrenciesField(JSONObject countryJson, String code, Map<String, List<String>> currenciesMap) {
        final List<String> currencies = new ArrayList<>();
        if (countryJson.has(FIELD_CURRENCIES)) {
            final JSONObject currenciesObj = countryJson.getJSONObject(FIELD_CURRENCIES);
            final Iterator<String> keys = currenciesObj.keys();
            while (keys.hasNext()) {
                final String key = keys.next();
                final JSONObject currencyDetail = currenciesObj.getJSONObject(key);
                final String currencyName = currencyDetail.getString(FIELD_NAME);
                currencies.add(currencyName);
            }
        }
        currenciesMap.put(code, currencies);
    }

    private void processTimezonesField(JSONObject countryJson, String code, Map<String, List<String>> timezonesMap) {
        final List<String> timezones = new ArrayList<>();
        if (countryJson.has(FIELD_TIMEZONES)) {
            final JSONArray timezonesArray = countryJson.getJSONArray(FIELD_TIMEZONES);
            for (int j = 0; j < timezonesArray.length(); j++) {
                timezones.add(timezonesArray.getString(j));
            }
        }
        timezonesMap.put(code, timezones);
    }

    private Country createCountry(String countryCode, CountryDataMaps dataMaps) {
        return new Country(
                countryCode,
                dataMaps.nameMap.get(countryCode),
                dataMaps.capitalMap.get(countryCode),
                dataMaps.regionMap.get(countryCode),
                dataMaps.subregionMap.get(countryCode),
                dataMaps.populationMap.getOrDefault(countryCode, 0),
                dataMaps.areaMap.getOrDefault(countryCode, 0.0),
                dataMaps.bordersMap.getOrDefault(countryCode, new ArrayList<>()),
                dataMaps.flagsMap.get(countryCode),
                dataMaps.languagesMap.getOrDefault(countryCode, new ArrayList<>()),
                dataMaps.currenciesMap.getOrDefault(countryCode, new ArrayList<>()),
                dataMaps.timezonesMap.getOrDefault(countryCode, new ArrayList<>())
        );
    }

    @Override
    public Country getCountryByName(String countryName) {
        Country result = null;
        for (Country country : getCountries()) {
            if (country.getName().equals(countryName)) {
                result = country;
                break;
            }
        }
        return result;
    }

    // -------------------- ADDED FOR COMPARE USE CASE --------------------

    @Override
    public List<String> getAllCountryNames() {
        final List<Country> countries = getCountries();
        final List<String> names = new ArrayList<>();
        for (final Country country : countries) {
            final String name = country.getName();
            if (name != null && !name.isEmpty()) {
                names.add(name);
            }
        }
        return names;
    }

    @Override
    public List<Country> getCountriesByNames(List<String> names) {
        final List<Country> all = getCountries();
        final Map<String, Country> byName = new HashMap<>();
        for (final Country country : all) {
            final String name = country.getName();
            if (name != null && !name.isEmpty()) {
                byName.put(name, country);
            }
        }

        final List<Country> result = new ArrayList<>();
        for (final String name : names) {
            final Country match = byName.get(name);
            if (match != null) {
                result.add(match);
            }
        }
        return result;
    }

    private static final class CountryDataMaps {
        private final Set<String> countryCodes = new HashSet<>();
        private final Map<String, String> nameMap = new HashMap<>();
        private final Map<String, String> capitalMap = new HashMap<>();
        private final Map<String, String> regionMap = new HashMap<>();
        private final Map<String, String> subregionMap = new HashMap<>();
        private final Map<String, Integer> populationMap = new HashMap<>();
        private final Map<String, Double> areaMap = new HashMap<>();
        private final Map<String, List<String>> bordersMap = new HashMap<>();
        private final Map<String, String> flagsMap = new HashMap<>();
        private final Map<String, List<String>> languagesMap = new HashMap<>();
        private final Map<String, List<String>> currenciesMap = new HashMap<>();
        private final Map<String, List<String>> timezonesMap = new HashMap<>();
    }
}

