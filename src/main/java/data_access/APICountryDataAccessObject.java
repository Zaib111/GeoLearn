package data_access;

import entity.Country;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.country.CountryDataAccessInterface;

import java.io.IOException;
import java.util.*;

public class APICountryDataAccessObject implements CountryDataAccessInterface {
    private final OkHttpClient client;
    private final String apiBase;

    public APICountryDataAccessObject() {
        this.client = new OkHttpClient().newBuilder().build();
        this.apiBase = "https://restcountries.com/v3.1/all?fields=cca3,";
    }

    @Override
    public List<Country> getCountries() {
        List<Country> countries = new ArrayList<>();

        Set<String> countryCodes = new HashSet<>();
        Map<String, String> nameMap = new HashMap<>();
        Map<String, String> capitalMap = new HashMap<>();
        Map<String, String> regionMap = new HashMap<>();
        Map<String, String> subregionMap = new HashMap<>();
        Map<String, Integer> populationMap = new HashMap<>();
        Map<String, Double> areaMap = new HashMap<>();
        Map<String, List<String>> bordersMap = new HashMap<>();
        Map<String, String> flagsMap = new HashMap<>();
        Map<String, List<String>> languagesMap = new HashMap<>();
        Map<String, List<String>> currenciesMap = new HashMap<>();
        Map<String, List<String>> timezonesMap = new HashMap<>();

        List<String> fieldGroups = List.of(
                "name",
                "capital",
                "region",
                "subregion",
                "population",
                "area",
                "borders",
                "flags",
                "languages",
                "currencies",
                "timezones"
        );

        fieldGroups.forEach(field -> {
            String url = apiBase.concat(field);
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response response;
            try {
                response = client.newCall(request).execute();

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = Objects.requireNonNull(response.body()).string();
                JSONArray countryArray = new JSONArray(responseBody);
                for (int i = 0; i < countryArray.length(); i++) {
                    JSONObject countryJson = countryArray.getJSONObject(i);
                    String code = countryJson.getString("cca3");

                    countryCodes.add(code);

                    switch (field) {
                        case "name":
                            JSONObject nameObj = countryJson.getJSONObject("name");
                            String commonName = nameObj.getString("common");
                            nameMap.put(code, commonName);
                            break;
                        case "capital":
                            if (countryJson.has("capital")) {
                                JSONArray capitalArray = countryJson.getJSONArray("capital");
                                if (capitalArray.length() > 0) {
                                    capitalMap.put(code, capitalArray.getString(0));
                                }
                            }
                            break;
                        case "region":
                            String region = countryJson.getString("region");
                            regionMap.put(code, region);
                            break;
                        case "subregion":
                            if (countryJson.has("subregion")) {
                                String subregion = countryJson.getString("subregion");
                                subregionMap.put(code, subregion);
                            }
                            break;
                        case "population":
                            int population = countryJson.getInt("population");
                            populationMap.put(code, population);
                            break;
                        case "area":
                            double area = countryJson.getDouble("area");
                            areaMap.put(code, area);
                            break;
                        case "borders":
                            List<String> borders = new ArrayList<>();
                            if (countryJson.has("borders")) {
                                JSONArray bordersArray = countryJson.getJSONArray("borders");
                                for (int j = 0; j < bordersArray.length(); j++) {
                                    borders.add(bordersArray.getString(j));
                                }
                            }
                            bordersMap.put(code, borders);
                            break;
                        case "flags":
                            JSONObject flagsObj = countryJson.getJSONObject("flags");
                            String flagUrl = flagsObj.getString("png");
                            flagsMap.put(code, flagUrl);
                            break;
                        case "languages":
                            List<String> languages = new ArrayList<>();
                            if (countryJson.has("languages")) {
                                JSONObject languagesObj = countryJson.getJSONObject("languages");
                                Iterator<String> keys = languagesObj.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    languages.add(languagesObj.getString(key));
                                }
                            }
                            languagesMap.put(code, languages);
                            break;
                        case "currencies":
                            List<String> currencies = new ArrayList<>();
                            if (countryJson.has("currencies")) {
                                JSONObject currenciesObj = countryJson.getJSONObject("currencies");
                                Iterator<String> keys = currenciesObj.keys();
                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    JSONObject currencyDetail = currenciesObj.getJSONObject(key);
                                    String currencyName = currencyDetail.getString("name");
                                    currencies.add(currencyName);
                                }
                            }
                            currenciesMap.put(code, currencies);
                            break;
                        case "timezones":
                            List<String> timezones = new ArrayList<>();
                            if (countryJson.has("timezones")) {
                                JSONArray timezonesArray = countryJson.getJSONArray("timezones");
                                for (int j = 0; j < timezonesArray.length(); j++) {
                                    timezones.add(timezonesArray.getString(j));
                                }
                            }
                            timezonesMap.put(code, timezones);
                            break;
                    }
                }
            }
            catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        });

        countryCodes.forEach(countryCode -> {
            Country country = new Country(
                countryCode,
                nameMap.get(countryCode),
                capitalMap.get(countryCode),
                regionMap.get(countryCode),
                subregionMap.get(countryCode),
                populationMap.getOrDefault(countryCode, 0),
                areaMap.getOrDefault(countryCode, 0.0),
                bordersMap.getOrDefault(countryCode, new ArrayList<>()),
                flagsMap.get(countryCode),
                languagesMap.getOrDefault(countryCode, new ArrayList<>()),
                currenciesMap.getOrDefault(countryCode, new ArrayList<>()),
                timezonesMap.getOrDefault(countryCode, new ArrayList<>())
            );

            countries.add(country);
        });
        return countries;
    }

    @Override
    public Country getCountry(String countryCode){
        for(Country country: getCountries()){
            if(country.getCode().equals(countryCode)){
                return country;
            }
        }
        return null;
    }
}