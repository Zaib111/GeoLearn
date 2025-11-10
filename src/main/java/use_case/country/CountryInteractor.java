package use_case.country;

import entity.Country;

import java.util.List;

public class CountryInteractor implements CountryInputBoundary {
    CountryDataAccessInterface countryDataAccessInterface;
    CountryOutputBoundary countryOutputBoundary;

    public CountryInteractor(CountryDataAccessInterface countryDataAccessInterface,
                             CountryOutputBoundary countryOutputBoundary) {
        this.countryDataAccessInterface = countryDataAccessInterface;
        this.countryOutputBoundary = countryOutputBoundary;
    }

    @Override
    public List<Country> getCountries() {
        return countryDataAccessInterface.getCountries();
    }
}
