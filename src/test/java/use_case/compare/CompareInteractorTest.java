package use_case.compare;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import app.entities.Country;
import app.use_cases.compare.CompareDataAccessInterface;
import app.use_cases.compare.CompareInputBoundary;
import app.use_cases.compare.CompareInteractor;
import app.use_cases.compare.CompareOutputBoundary;
import app.use_cases.compare.CompareOutputData;

/**
 * Tests for the CompareInteractor.
 */
public class CompareInteractorTest {

    /**
     * Helper method to create some sample countries.
     */
    private List<Country> createTestCountries() {
        return Arrays.asList(
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
                        Arrays.asList("UTC-05:00"))
        );
    }

    /**
     * Helper method to create a CompareDataAccessInterface mock backed by a list of countries.
     */
    private CompareDataAccessInterface createCompareDAO(List<Country> countries) {
        return new CompareDataAccessInterface() {
            @Override
            public List<String> getAllCountryNames() {
                List<String> names = new ArrayList<>();
                for (Country c : countries) {
                    names.add(c.getName());
                }
                return names;
            }

            @Override
            public List<Country> getCountriesByNames(List<String> names) {
                List<Country> result = new ArrayList<>();
                for (String name : names) {
                    for (Country c : countries) {
                        if (c.getName().equals(name)) {
                            result.add(c);
                            break;
                        }
                    }
                }
                return result;
            }
        };
    }

    // ============ loadAvailableCountries() Tests ============

    @Test
    public void testLoadAvailableCountriesSuccess() {
        List<Country> countries = createTestCountries();
        CompareDataAccessInterface dao = createCompareDAO(countries);

        final boolean[] successCalled = new boolean[1];
        final boolean[] failCalled = new boolean[1];
        final List<String>[] receivedNames = new List[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                successCalled[0] = true;
                receivedNames[0] = countryNames;
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                // not used in this test
            }

            @Override
            public void prepareFailView(String errorMessage) {
                failCalled[0] = true;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.loadAvailableCountries();

        assertTrue("prepareCountriesList should be called", successCalled[0]);
        assertFalse("prepareFailView should not be called", failCalled[0]);
        assertNotNull(receivedNames[0]);
        assertEquals(3, receivedNames[0].size());
        assertTrue(receivedNames[0].contains("Canada"));
        assertTrue(receivedNames[0].contains("Brazil"));
        assertTrue(receivedNames[0].contains("United States"));
    }

    @Test
    public void testLoadAvailableCountriesFailureWhenEmptyList() {
        // DAO returns empty list
        CompareDataAccessInterface dao = new CompareDataAccessInterface() {
            @Override
            public List<String> getAllCountryNames() {
                return new ArrayList<>();
            }

            @Override
            public List<Country> getCountriesByNames(List<String> names) {
                return new ArrayList<>();
            }
        };

        final boolean[] successCalled = new boolean[1];
        final String[] errorMessage = new String[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                successCalled[0] = true;
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                // not used here
            }

            @Override
            public void prepareFailView(String message) {
                errorMessage[0] = message;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.loadAvailableCountries();

        assertFalse("prepareCountriesList should not be called on failure", successCalled[0]);
        assertEquals("Failed to load country list.", errorMessage[0]);
    }

    @Test
    public void testLoadAvailableCountriesFailureWhenNull() {
        // DAO returns null
        CompareDataAccessInterface dao = new CompareDataAccessInterface() {
            @Override
            public List<String> getAllCountryNames() {
                return null;
            }

            @Override
            public List<Country> getCountriesByNames(List<String> names) {
                return new ArrayList<>();
            }
        };

        final boolean[] successCalled = new boolean[1];
        final String[] errorMessage = new String[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                successCalled[0] = true;
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                // not used here
            }

            @Override
            public void prepareFailView(String message) {
                errorMessage[0] = message;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.loadAvailableCountries();

        assertFalse("prepareCountriesList should not be called on failure", successCalled[0]);
        assertEquals("Failed to load country list.", errorMessage[0]);
    }

    // ============ execute() Tests ============

    @Test
    public void testExecuteFailsWhenSelectedNamesNull() {
        List<Country> countries = createTestCountries();
        CompareDataAccessInterface dao = createCompareDAO(countries);

        final String[] errorMessage = new String[1];
        final boolean[] successCalled = new boolean[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                // not used here
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String message) {
                errorMessage[0] = message;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.execute(null);

        assertFalse("prepareSuccessView should not be called", successCalled[0]);
        assertEquals("Select at least two countries to compare.", errorMessage[0]);
    }

    @Test
    public void testExecuteFailsWhenLessThanTwoCountries() {
        List<Country> countries = createTestCountries();
        CompareDataAccessInterface dao = createCompareDAO(countries);

        final String[] errorMessage = new String[1];
        final boolean[] successCalled = new boolean[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                // not used here
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String message) {
                errorMessage[0] = message;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.execute(Arrays.asList("Canada"));

        assertFalse("prepareSuccessView should not be called", successCalled[0]);
        assertEquals("Select at least two countries to compare.", errorMessage[0]);
    }

    @Test
    public void testExecuteFailsWhenCountriesNotFound() {
        List<Country> countries = createTestCountries();
        CompareDataAccessInterface dao = createCompareDAO(countries);

        final String[] errorMessage = new String[1];
        final boolean[] successCalled = new boolean[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                // not used here
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                successCalled[0] = true;
            }

            @Override
            public void prepareFailView(String message) {
                errorMessage[0] = message;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.execute(Arrays.asList("Canada", "FakeCountry"));

        assertFalse("prepareSuccessView should not be called", successCalled[0]);
        assertEquals("Some selected countries could not be found.", errorMessage[0]);
    }

    @Test
    public void testExecuteSuccess() {
        List<Country> countries = createTestCountries();
        CompareDataAccessInterface dao = createCompareDAO(countries);

        final boolean[] successCalled = new boolean[1];
        final String[] errorMessage = new String[1];
        final CompareOutputData[] capturedOutput = new CompareOutputData[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                // not used here
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                successCalled[0] = true;
                capturedOutput[0] = outputData;
            }

            @Override
            public void prepareFailView(String message) {
                errorMessage[0] = message;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.execute(Arrays.asList("Canada", "United States"));

        assertTrue("prepareSuccessView should be called", successCalled[0]);
        assertNull("prepareFailView should not be called", errorMessage[0]);
        assertNotNull(capturedOutput[0]);
        assertEquals(2, capturedOutput[0].getSelectedCountries().size());
        assertEquals("Canada", capturedOutput[0].getSelectedCountries().get(0).getName());
        assertEquals("United States", capturedOutput[0].getSelectedCountries().get(1).getName());
    }

    @Test
    public void testExecuteSuccessEvenIfDAOOrderDiffers() {
        // DAO that returns countries in reverse order of input
        List<Country> countries = createTestCountries();
        CompareDataAccessInterface dao = new CompareDataAccessInterface() {
            @Override
            public List<String> getAllCountryNames() {
                List<String> names = new ArrayList<>();
                for (Country c : countries) {
                    names.add(c.getName());
                }
                return names;
            }

            @Override
            public List<Country> getCountriesByNames(List<String> names) {
                List<Country> result = new ArrayList<>();
                for (int i = names.size() - 1; i >= 0; i--) {
                    String name = names.get(i);
                    for (Country c : countries) {
                        if (c.getName().equals(name)) {
                            result.add(c);
                            break;
                        }
                    }
                }
                return result;
            }
        };

        final boolean[] successCalled = new boolean[1];
        final String[] errorMessage = new String[1];
        final CompareOutputData[] capturedOutput = new CompareOutputData[1];

        CompareOutputBoundary presenter = new CompareOutputBoundary() {
            @Override
            public void prepareCountriesList(List<String> countryNames) {
                // not used here
            }

            @Override
            public void prepareSuccessView(CompareOutputData outputData) {
                successCalled[0] = true;
                capturedOutput[0] = outputData;
            }

            @Override
            public void prepareFailView(String message) {
                errorMessage[0] = message;
            }
        };

        CompareInputBoundary interactor = new CompareInteractor(dao, presenter);
        interactor.execute(Arrays.asList("Canada", "United States"));

        assertTrue("prepareSuccessView should be called", successCalled[0]);
        assertNull("prepareFailView should not be called", errorMessage[0]);
        assertNotNull(capturedOutput[0]);
        assertEquals(2, capturedOutput[0].getSelectedCountries().size());
        // We don't assert order here, just that both are present
        List<String> names = Arrays.asList(
                capturedOutput[0].getSelectedCountries().get(0).getName(),
                capturedOutput[0].getSelectedCountries().get(1).getName()
        );
        assertTrue(names.contains("Canada"));
        assertTrue(names.contains("United States"));
    }
}
