package app.use_cases.detail;

import app.entities.Country;

import java.util.List;
import java.util.Optional;

/**
 * Data structure representing the output data for the Detail Use Case.
 * Contains primitive data types extracted from the Country entity, ready to be passed to the Presenter.
 */
public class DetailOutputData {
    // Basic identifiers
    private final String countryCode;
    private final String countryName;

    // Geographic and political details
    private final Optional<String> capital;
    private final String region;
    private final Optional<String> subregion;

    // Statistical data
    private final long population;
    private final double areaKm2;

    // Lists of associated data
    private final List<String> borders;
    private final String flagUrl;
    private final List<String> languages;
    private final List<String> currencies;
    private final List<String> timezones;

    /**
     * Constructor that extracts necessary data from a Country entity.
     *
     * @param country The Country entity containing the raw domain data.
     */
    public DetailOutputData(Country country){
        // Extract basic identification data
        countryCode = country.getCode();
        countryName = country.getName();

        // Extract geographic details
        capital = country.getCapital();
        region = country.getRegion();
        subregion = country.getSubregion();

        // Extract statistical data
        population = country.getPopulation();
        areaKm2 = country.getAreaKm2();

        // Extract list-based properties and the flag
        borders = country.getBorders();
        flagUrl = country.getFlagUrl();
        languages = country.getLanguages();
        currencies = country.getCurrencies();
        timezones = country.getTimezones();
    }

    /**
     * Gets the unique country code (e.g., ISO alpha code).
     * @return The country code.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Gets the common name of the country.
     * @return The country name.
     */
    public String getCountryName() {
        return countryName;
    }

    /**
     * Gets the capital city, wrapped in an Optional as some territories may not have one.
     * @return An Optional containing the capital name if present.
     */
    public Optional<String> getCapital() {
        return capital;
    }

    /**
     * Gets the geographic region (continent).
     * @return The region name.
     */
    public String getRegion() {
        return region;
    }

    /**
     * Gets the subregion, wrapped in an Optional.
     * @return An Optional containing the subregion name if present.
     */
    public Optional<String> getSubregion() {
        return subregion;
    }

    /**
     * Gets the population count.
     * @return The population as a long.
     */
    public long getPopulation() {
        return population;
    }

    /**
     * Gets the land area in square kilometers.
     * @return The area.
     */
    public double getAreaKm2() {
        return areaKm2;
    }

    /**
     * Gets a list of border country codes.
     * @return A List of strings representing neighboring country codes.
     */
    public List<String> getBorders() {
        return borders;
    }

    /**
     * Gets the URL for the country's flag image.
     * @return The flag URL string.
     */
    public String getFlagUrl() {
        return flagUrl;
    }

    /**
     * Gets the list of official languages.
     * @return A List of language names.
     */
    public List<String> getLanguages() {
        return languages;
    }

    /**
     * Gets the list of currencies used.
     * @return A List of currency names/codes.
     */
    public List<String> getCurrencies() {
        return currencies;
    }

    /**
     * Gets the list of timezones the country spans.
     * @return A List of timezone strings.
     */
    public List<String> getTimezones() {
        return timezones;
    }
}