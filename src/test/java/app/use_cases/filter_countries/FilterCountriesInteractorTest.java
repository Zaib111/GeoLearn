package app.use_cases.filter_countries;

import app.entities.Country;
import app.use_cases.filter_country.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class FilterCountriesInteractorTest {

    /**
     * Helper method to create a FilterCountriesDataAccessInterface mock.
     */
    private FilterCountriesDataAccessInterface  createFilterCountriesDataAccess() {
        return () -> Arrays.asList(
                new Country("CAN", "Canada", "Ottawa", "Americas", "Northern America",
                        38_000_000L, 9_984_670.0, Arrays.asList("USA"), "https://flagcdn.com/ca.svg",
                        Arrays.asList("English", "French"), Arrays.asList("Canadian Dollar"),
                        Arrays.asList("UTC-05:00")),
                new Country("BRA", "Brazil", "Brasília", "Americas", "South America",
                        215_000_000L, 8_515_767.0, new ArrayList<String>(), "https://flagcdn.com/br.svg",
                        Arrays.asList("Portuguese"), Arrays.asList("Brazilian Real"),
                        Arrays.asList("UTC-03:00")),
                new Country("USA", "United States", "Washington, D.C.", "Americas", "Northern America",
                        331_000_000L, 9_833_520.0, Arrays.asList("CAN", "MEX"), "https://flagcdn.com/us.svg",
                        Arrays.asList("English"), Arrays.asList("United States Dollar"),
                        Arrays.asList("UTC-05:00")),
                new Country("TCD", "Chad", "N'Djamena", "Africa", "Middle Africa",
                        19_000_000L, 1_284_000.0, new ArrayList<String>(), "https://flagcdn.com/td.svg",
                        Arrays.asList("Arabic", "French"), Arrays.asList("Central African CFA franc"), Arrays.asList("UTC+01:00"))
        );
    }

    @Test
    public void testFilterCountriesSingleCountryByName() {
        // Make an array that contains a single List of Country
        final List<Country>[] receivedOutput = new List[]{new ArrayList<>()};

        FilterCountriesDataAccessInterface filterCountriesDataAccessInterface = createFilterCountriesDataAccess();
        FilterCountriesOutputBoundary filterCountriesOutputBoundary = new FilterCountriesOutputBoundary() {
            @Override
            public void presentFilteredCountries(FilterCountriesOutputData outputData) {
                receivedOutput[0] = outputData.getCountries();
            }
        };
        FilterCountriesInputBoundary filterCountriesInputeractor = new FilterCountriesInteractor(filterCountriesDataAccessInterface, filterCountriesOutputBoundary);
        FilterCountriesInputData testData = new FilterCountriesInputData("Canada", "Any", "Any");
        // "Canada" should only match Canada
        List<Country> expectedOutput = Arrays.asList(
                new Country("CAN", "Canada", "Ottawa", "Americas", "Northern America",
                        38_000_000L, 9_984_670.0, Arrays.asList("USA"), "https://flagcdn.com/ca.svg",
                        Arrays.asList("English", "French"), Arrays.asList("Canadian Dollar"),
                        Arrays.asList("UTC-05:00"))
        );

        filterCountriesInputeractor.filterCountries(testData);

        // Check that only one country is returned
        assertEquals(1, receivedOutput[0].size());

        // Check that name of country (Canada) matches
        assertSame(expectedOutput.get(0).getName(), receivedOutput[0].get(0).getName());
    }

    @Test
    public void testFilterCountriesSingleCountryByNameCaseSensitive() {
        // Make an array that contains a single List of Country
        final List<Country>[] receivedOutput = new List[]{new ArrayList<>()};

        FilterCountriesDataAccessInterface filterCountriesDataAccessInterface = createFilterCountriesDataAccess();
        FilterCountriesOutputBoundary filterCountriesOutputBoundary = new FilterCountriesOutputBoundary() {
            @Override
            public void presentFilteredCountries(FilterCountriesOutputData outputData) {
                receivedOutput[0] = outputData.getCountries();
            }
        };
        FilterCountriesInputBoundary filterCountriesInputeractor = new FilterCountriesInteractor(filterCountriesDataAccessInterface, filterCountriesOutputBoundary);
        FilterCountriesInputData testData = new FilterCountriesInputData("chad", "Any", "Any");
        // "chad" should only match Chad
        List<Country> expectedOutput = Arrays.asList(
                new Country("TCD", "Chad", "N'Djamena", "Africa", "Middle Africa",
                        19_000_000L, 1_284_000.0, new ArrayList<String>(), "https://flagcdn.com/td.svg",
                        Arrays.asList("Arabic", "French"), Arrays.asList("Central African CFA franc"), Arrays.asList("UTC+01:00"))
        );

        filterCountriesInputeractor.filterCountries(testData);

        // Check that only one country is returned
        assertEquals(1, receivedOutput[0].size());

        // Check that name of country (Canada) matches
        assertSame(expectedOutput.get(0).getName(), receivedOutput[0].get(0).getName());
    }

    @Test
    public void testFilterCountriesMultipleCountriesByName() {
        // Make an array that contains a single List of Country
        final List<Country>[] receivedOutput = new List[]{new ArrayList<>()};

        FilterCountriesDataAccessInterface filterCountriesDataAccessInterface = createFilterCountriesDataAccess();
        FilterCountriesOutputBoundary filterCountriesOutputBoundary = new FilterCountriesOutputBoundary() {
            @Override
            public void presentFilteredCountries(FilterCountriesOutputData outputData) {
                receivedOutput[0] = outputData.getCountries();
            }
        };
        FilterCountriesInputBoundary filterCountriesInputeractor = new FilterCountriesInteractor(filterCountriesDataAccessInterface, filterCountriesOutputBoundary);
        FilterCountriesInputData testData = new FilterCountriesInputData("n", "Any", "Any");
        // "n" should match both "United States" and "Canada" but not "Brazil"
        List<Country> expectedOutput = Arrays.asList(
                new Country("CAN", "Canada", "Ottawa", "Americas", "Northern America",
                        38_000_000L, 9_984_670.0, Arrays.asList("USA"), "https://flagcdn.com/ca.svg",
                        Arrays.asList("English", "French"), Arrays.asList("Canadian Dollar"),
                        Arrays.asList("UTC-05:00")),
                new Country("USA", "United States", "Washington, D.C.", "Americas", "Northern America",
                        331_000_000L, 9_833_520.0, Arrays.asList("CAN", "MEX"), "https://flagcdn.com/us.svg",
                        Arrays.asList("English"), Arrays.asList("United States Dollar"),
                        Arrays.asList("UTC-05:00"))
        );

        filterCountriesInputeractor.filterCountries(testData);

        // Check that two countries are returned
        assertEquals(2, receivedOutput[0].size());

        // Check that names of returned countries are the same
        HashSet<String> expectedNames = new HashSet<>();
        for (Country country : expectedOutput) {
            expectedNames.add(country.getName());
        }

        HashSet<String> receivedNames = new HashSet<>();
        for (Country country : receivedOutput[0]) {
            receivedNames.add(country.getName());
        }
        assertEquals(expectedNames, receivedNames);
    }

    @Test
    public void testFilterCountriesSingleCountryByRegion() {
        // Make an array that contains a single List of Country
        final List<Country>[] receivedOutput = new List[]{new ArrayList<>()};

        FilterCountriesDataAccessInterface filterCountriesDataAccessInterface = createFilterCountriesDataAccess();
        FilterCountriesOutputBoundary filterCountriesOutputBoundary = new FilterCountriesOutputBoundary() {
            @Override
            public void presentFilteredCountries(FilterCountriesOutputData outputData) {
                receivedOutput[0] = outputData.getCountries();
            }
        };
        FilterCountriesInputBoundary filterCountriesInputeractor = new FilterCountriesInteractor(filterCountriesDataAccessInterface, filterCountriesOutputBoundary);
        FilterCountriesInputData testData = new FilterCountriesInputData("", "Africa", "Any");
        // "Africa" should only match Chad
        List<Country> expectedOutput = Arrays.asList(
                new Country("TCD", "Chad", "N'Djamena", "Africa", "Middle Africa",
                        19_000_000L, 1_284_000.0, new ArrayList<String>(), "https://flagcdn.com/td.svg",
                        Arrays.asList("Arabic", "French"), Arrays.asList("Central African CFA franc"), Arrays.asList("UTC+01:00"))
        );

        filterCountriesInputeractor.filterCountries(testData);

        // Check that only one country is returned
        assertEquals(1, receivedOutput[0].size());

        // Check that name of country (Canada) matches
        assertSame(expectedOutput.get(0).getName(), receivedOutput[0].get(0).getName());
    }

    @Test
    public void testFilterCountriesMultipleCountriesBySubRegion() {
        // Make an array that contains a single List of Country
        final List<Country>[] receivedOutput = new List[]{new ArrayList<>()};

        FilterCountriesDataAccessInterface filterCountriesDataAccessInterface = createFilterCountriesDataAccess();
        FilterCountriesOutputBoundary filterCountriesOutputBoundary = new FilterCountriesOutputBoundary() {
            @Override
            public void presentFilteredCountries(FilterCountriesOutputData outputData) {
                receivedOutput[0] = outputData.getCountries();
            }
        };
        FilterCountriesInputBoundary filterCountriesInputeractor = new FilterCountriesInteractor(filterCountriesDataAccessInterface, filterCountriesOutputBoundary);
        FilterCountriesInputData testData = new FilterCountriesInputData("", "Americas", "Northern America");
        // "Northern America" should match both "United States" and "Canada" but not "Brazil"
        List<Country> expectedOutput = Arrays.asList(
                new Country("CAN", "Canada", "Ottawa", "Americas", "Northern America",
                        38_000_000L, 9_984_670.0, Arrays.asList("USA"), "https://flagcdn.com/ca.svg",
                        Arrays.asList("English", "French"), Arrays.asList("Canadian Dollar"),
                        Arrays.asList("UTC-05:00")),
                new Country("USA", "United States", "Washington, D.C.", "Americas", "Northern America",
                        331_000_000L, 9_833_520.0, Arrays.asList("CAN", "MEX"), "https://flagcdn.com/us.svg",
                        Arrays.asList("English"), Arrays.asList("United States Dollar"),
                        Arrays.asList("UTC-05:00"))
        );

        filterCountriesInputeractor.filterCountries(testData);

        // Check that two countries are returned
        assertEquals(2, receivedOutput[0].size());

        // Check that names of returned countries are the same
        HashSet<String> expectedNames = new HashSet<>();
        for (Country country : expectedOutput) {
            expectedNames.add(country.getName());
        }

        HashSet<String> receivedNames = new HashSet<>();
        for (Country country : receivedOutput[0]) {
            receivedNames.add(country.getName());
        }
        assertEquals(expectedNames, receivedNames);
    }

    @Test
    public void testFilterCountriesAllCountries() {
        // Make an array that contains a single List of Country
        final List<Country>[] receivedOutput = new List[]{new ArrayList<>()};

        FilterCountriesDataAccessInterface filterCountriesDataAccessInterface = createFilterCountriesDataAccess();
        FilterCountriesOutputBoundary filterCountriesOutputBoundary = new FilterCountriesOutputBoundary() {
            @Override
            public void presentFilteredCountries(FilterCountriesOutputData outputData) {
                receivedOutput[0] = outputData.getCountries();
            }
        };
        FilterCountriesInputBoundary filterCountriesInputeractor = new FilterCountriesInteractor(filterCountriesDataAccessInterface, filterCountriesOutputBoundary);
        FilterCountriesInputData testData = new FilterCountriesInputData("", "Any", "Any");
        List<Country> expectedOutput = Arrays.asList(
                new Country("CAN", "Canada", "Ottawa", "Americas", "Northern America",
                        38_000_000L, 9_984_670.0, Arrays.asList("USA"), "https://flagcdn.com/ca.svg",
                        Arrays.asList("English", "French"), Arrays.asList("Canadian Dollar"),
                        Arrays.asList("UTC-05:00")),
                new Country("BRA", "Brazil", "Brasília", "Americas", "South America",
                        215_000_000L, 8_515_767.0, new ArrayList<String>(), "https://flagcdn.com/br.svg",
                        Arrays.asList("Portuguese"), Arrays.asList("Brazilian Real"),
                        Arrays.asList("UTC-03:00")),
                new Country("USA", "United States", "Washington, D.C.", "Americas", "Northern America",
                        331_000_000L, 9_833_520.0, Arrays.asList("CAN", "MEX"), "https://flagcdn.com/us.svg",
                        Arrays.asList("English"), Arrays.asList("United States Dollar"),
                        Arrays.asList("UTC-05:00")),
                new Country("TCD", "Chad", "N'Djamena", "Africa", "Middle Africa",
                        19_000_000L, 1_284_000.0, new ArrayList<String>(), "https://flagcdn.com/td.svg",
                        Arrays.asList("Arabic", "French"), Arrays.asList("Central African CFA franc"), Arrays.asList("UTC+01:00"))
        );

        filterCountriesInputeractor.filterCountries(testData);

        // Check that four countries are returned
        assertEquals(4, receivedOutput[0].size());

        // Check that names of returned countries are the same
        HashSet<String> expectedNames = new HashSet<>();
        for (Country country : expectedOutput) {
            expectedNames.add(country.getName());
        }

        HashSet<String> receivedNames = new HashSet<>();
        for (Country country : receivedOutput[0]) {
            receivedNames.add(country.getName());
        }
        assertEquals(expectedNames, receivedNames);
    }

    @Test
    public void testFilterCountriesNoCountries() {
        // Make an array that contains a single List of Country
        final List<Country>[] receivedOutput = new List[]{new ArrayList<>()};

        FilterCountriesDataAccessInterface filterCountriesDataAccessInterface = createFilterCountriesDataAccess();
        FilterCountriesOutputBoundary filterCountriesOutputBoundary = new FilterCountriesOutputBoundary() {
            @Override
            public void presentFilteredCountries(FilterCountriesOutputData outputData) {
                receivedOutput[0] = outputData.getCountries();
            }
        };
        FilterCountriesInputBoundary filterCountriesInputeractor = new FilterCountriesInteractor(filterCountriesDataAccessInterface, filterCountriesOutputBoundary);
        // There is no country called "ABCDEFG" in Eastern Asia
        FilterCountriesInputData testData = new FilterCountriesInputData("ABCDEFG", "Asia", "Eastern Asia");

        filterCountriesInputeractor.filterCountries(testData);

        // Check that zero countries are returned
        assertEquals(0, receivedOutput[0].size());
    }
}
