package app.use_cases.filter_country;

public class FilterCountriesInputData {
    private final String searchTerm;
    private final String region;
    private final String subregion;

    public FilterCountriesInputData(String searchTerm, String region, String subregion) {
        this.searchTerm = searchTerm;
        this.region = region;
        this.subregion = subregion;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public String getRegion() {
        return region;
    }

    public String getSubregion() {
        return subregion;
    }
}
