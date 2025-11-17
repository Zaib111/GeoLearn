package use_case.detail;

import java.util.*;
import entity.Country;

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
}
