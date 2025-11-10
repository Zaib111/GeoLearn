package use_case.collection;

import entity.Country;

import java.util.*;

public class CollectionOutputData {
    private List<Country> countriesInCollection;

    public CollectionOutputData(List<Country> countriesInCollection) { this.countriesInCollection = countriesInCollection; }

    public List<Country> getCountriesInCollection() { return countriesInCollection; }

}