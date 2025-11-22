package app.entities;

import lombok.Getter;

import java.util.*;

@Getter
public class Country {

    private final String code; // Country ISO code
    private final String name; // Country name
    private final String capital; // Name of the capital city (nullable)
    private final String region; // Region (e.g. Europe)
    private final String subregion; // Sub-region (nullable)
    private final long population; // Population count
    private final double areaKm2; // Land area
    private final List<String> borders; // Neighbour country codes
    private final String flagUrl; // Image link
    private final List<String> languages; // Country's spoken languages
    private final List<String> currencies; // Country's currencies
    private final List<String> timezones; // Country's time zones

    public Country(
            String code,
            String name,
            String capital,
            String region,
            String subregion,
            long population,
            double areaKm2,
            List<String> borders,
            String flagUrl,
            List<String> languages,
            List<String> currencies,
            List<String> timezones
    ) {
        this.code = Objects.requireNonNull(code, "code");
        this.name = Objects.requireNonNull(name, "name");
        this.capital = capital; // may be null
        this.region = Objects.requireNonNull(region, "region");
        this.subregion = subregion; // may be null
        this.population = population;
        this.areaKm2 = areaKm2;
        // allow null lists and convert to empty lists, make defensive unmodifiable copies
        this.borders = Collections.unmodifiableList(new ArrayList<>(borders == null ? Collections.emptyList() : borders));
        this.flagUrl = Objects.requireNonNull(flagUrl, "flagUrl");
        this.languages = Collections.unmodifiableList(new ArrayList<>(languages == null ? Collections.emptyList() : languages));
        this.currencies = Collections.unmodifiableList(new ArrayList<>(currencies == null ? Collections.emptyList() : currencies));
        this.timezones = Collections.unmodifiableList(new ArrayList<>(timezones == null ? Collections.emptyList() : timezones));
    }

    // Custom getters for Optional fields
    public Optional<String> getCapital() {
        return Optional.ofNullable(capital);
    }

    public Optional<String> getSubregion() {
        return Optional.ofNullable(subregion);
    }
}
