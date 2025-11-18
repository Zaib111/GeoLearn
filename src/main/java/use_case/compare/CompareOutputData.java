package use_case.compare;

import java.util.List;

public class CompareOutputData {

    private final List<CountryRow> countries;

    // Flags used by presenter to know what differs
    private final boolean populationDiffers;
    private final boolean areaDiffers;
    private final boolean densityDiffers;
    private final boolean capitalDiffers;
    private final boolean languagesDiffer;
    private final boolean currenciesDiffer;

    public CompareOutputData(List<CountryRow> countries,
                             boolean populationDiffers,
                             boolean areaDiffers,
                             boolean densityDiffers,
                             boolean capitalDiffers,
                             boolean languagesDiffer,
                             boolean currenciesDiffer) {
        this.countries = countries;
        this.populationDiffers = populationDiffers;
        this.areaDiffers = areaDiffers;
        this.densityDiffers = densityDiffers;
        this.capitalDiffers = capitalDiffers;
        this.languagesDiffer = languagesDiffer;
        this.currenciesDiffer = currenciesDiffer;
    }

    public List<CountryRow> getCountries() {
        return countries;
    }

    public boolean isPopulationDiffers() {
        return populationDiffers;
    }

    public boolean isAreaDiffers() {
        return areaDiffers;
    }

    public boolean isDensityDiffers() {
        return densityDiffers;
    }

    public boolean isCapitalDiffers() {
        return capitalDiffers;
    }

    public boolean isLanguagesDiffer() {
        return languagesDiffer;
    }

    public boolean isCurrenciesDiffer() {
        return currenciesDiffer;
    }

    /**
     * Represents one country's data in the comparison.
     */
    public static class CountryRow {
        private final String name;
        private final String capital;
        private final String region;
        private final String subregion;
        private final long population;
        private final double area;
        private final double density;
        private final List<String> languages;
        private final List<String> currencies;

        public CountryRow(String name,
                          String capital,
                          String region,
                          String subregion,
                          long population,
                          double area,
                          double density,
                          List<String> languages,
                          List<String> currencies) {
            this.name = name;
            this.capital = capital;
            this.region = region;
            this.subregion = subregion;
            this.population = population;
            this.area = area;
            this.density = density;
            this.languages = languages;
            this.currencies = currencies;
        }

        public String getName() { return name; }
        public String getCapital() { return capital; }
        public String getRegion() { return region; }
        public String getSubregion() { return subregion; }
        public long getPopulation() { return population; }
        public double getArea() { return area; }
        public double getDensity() { return density; }
        public List<String> getLanguages() { return languages; }
        public List<String> getCurrencies() { return currencies; }
    }
}
