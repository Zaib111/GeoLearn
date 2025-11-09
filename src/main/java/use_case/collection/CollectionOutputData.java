package use_case.collection;

import java.util.*;

public class CollectionOutputData {
    private List<String> countries; // countries in a certain collection that the user chooses to view

    public CollectionOutputData(List<String> countries) { this.countries = countries; }

    public List<String> getCountries() { return countries; }

}