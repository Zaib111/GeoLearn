package app.use_cases.detail;

import app.entities.Country;

import java.util.List;
import java.util.Optional;

/**
 * Output data for detail use case.
 */
public class DetailOutputData {
    private final String countryCode;
    private final String countryName;
    private final Optional<String> capital;
    private final String region;
    private final Optional<String> subregion;
    private final long population;
    private final double areaKm2;
    private final List<String> borders;
    private final String flagUrl;
    private final List<String> languages;
    private final List<String> currencies;
    private final List<String> timezones;

    /**
     * Constructor.
     *
     * @param country the country
     */
    public DetailOutputData(final Country country) {
        countryCode = country.getCode();
        countryName = country.getName();
        capital = country.getCapital();
        region = country.getRegion();
        subregion = country.getSubregion();
        population = country.getPopulation();
        areaKm2 = country.getAreaKm2();
        borders = country.getBorders();
        flagUrl = country.getFlagUrl();
        languages = country.getLanguages();
        currencies = country.getCurrencies();
        timezones = country.getTimezones();
    }

    /**
     * Get the country code.
     *
     * @return the country code
     */
    public final String getCountryCode() {
        return countryCode;
    }

    /**
     * Get the country name.
     *
     * @return the country name
     */
    public final String getCountryName() {
        return countryName;
    }

    /**
     * Get the capital.
     *
     * @return the capital
     */
    public final Optional<String> getCapital() {
        return capital;
    }

    /**
     * Get the region.
     *
     * @return the region
     */
    public final String getRegion() {
        return region;
    }

    /**
     * Get the subregion.
     *
     * @return the subregion
     */
    public final Optional<String> getSubregion() {
        return subregion;
    }

    /**
     * Get the population.
     *
     * @return the population
     */
    public final long getPopulation() {
        return population;
    }

    /**
     * Get the area in km2.
     *
     * @return the area in km2
     */
    public final double getAreaKm2() {
        return areaKm2;
    }

    /**
     * Get the borders.
     *
     * @return the borders
     */
    public final List<String> getBorders() {
        return borders;
    }

    /**
     * Get the flag URL.
     *
     * @return the flag URL
     */
    public final String getFlagUrl() {
        return flagUrl;
    }

    /**
     * Get the languages.
     *
     * @return the languages
     */
    public final List<String> getLanguages() {
        return languages;
    }

    /**
     * Get the currencies.
     *
     * @return the currencies
     */
    public final List<String> getCurrencies() {
        return currencies;
    }

    /**
     * Get the timezones.
     *
     * @return the timezones
     */
    public final List<String> getTimezones() {
        return timezones;
    }
}
