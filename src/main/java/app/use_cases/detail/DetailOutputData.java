package app.use_cases.detail;

import app.entities.Country;

import java.util.List;
import java.util.Optional;

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
    public DetailOutputData(Country country){
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
    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public Optional<String> getCapital() {
        return capital;
    }

    public String getRegion() {
        return region;
    }

    public Optional<String> getSubregion() {
        return subregion;
    }

    public long getPopulation() {
        return population;
    }

    public double getAreaKm2() {
        return areaKm2;
    }

    public List<String> getBorders() {
        return borders;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public List<String> getTimezones() {
        return timezones;
    }
}
