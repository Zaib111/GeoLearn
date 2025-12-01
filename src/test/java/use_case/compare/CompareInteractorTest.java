package app.use_cases.compare;

import app.entities.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CompareInteractor to achieve 100% code coverage.
 */
class CompareInteractorTest {

    private StubDataAccess dataAccess;
    private StubPresenter presenter;
    private CompareInteractor interactor;

    @BeforeEach
    void setUp() {
        dataAccess = new StubDataAccess();
        presenter = new StubPresenter();
        interactor = new CompareInteractor(dataAccess, presenter);
    }

    // ---------------------------------------------------------
    // loadAvailableCountries()
    // ---------------------------------------------------------

    @Test
    void loadAvailableCountries_success_callsPrepareCountriesList() {
        // Arrange
        dataAccess.allCountryNames = Arrays.asList("Canada", "France");

        // Act
        interactor.loadAvailableCountries();

        // Assert
        assertEquals(1, presenter.prepareCountriesListCalls);
        assertEquals(Arrays.asList("Canada", "France"), presenter.lastCountryNames);
        assertEquals(0, presenter.failCalls);
    }

    @Test
    void loadAvailableCountries_failure_whenNamesNull_callsPrepareFailView() {
        // Arrange
        dataAccess.allCountryNames = null;

        // Act
        interactor.loadAvailableCountries();

        // Assert
        assertEquals(0, presenter.prepareCountriesListCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Failed to load country list.", presenter.lastErrorMessage);
    }

    @Test
    void loadAvailableCountries_failure_whenNamesEmpty_callsPrepareFailView() {
        // Arrange
        dataAccess.allCountryNames = Collections.emptyList();

        // Act
        interactor.loadAvailableCountries();

        // Assert
        assertEquals(0, presenter.prepareCountriesListCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Failed to load country list.", presenter.lastErrorMessage);
    }

    // ---------------------------------------------------------
    // execute(...)
    // ---------------------------------------------------------

    @Test
    void execute_failure_whenSelectedNamesNull() {
        // Act
        interactor.execute(null);

        // Assert
        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Select at least two countries to compare.", presenter.lastErrorMessage);
    }

    @Test
    void execute_failure_whenLessThanTwoCountries() {
        // Act
        interactor.execute(Collections.singletonList("Canada"));

        // Assert
        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Select at least two countries to compare.", presenter.lastErrorMessage);
    }

    @Test
    void execute_failure_whenSomeCountriesNotFound() {
        // Arrange
        List<String> requested = Arrays.asList("Canada", "France");
        // Data access will only "find" one country, so sizes differ.
        dataAccess.countriesByNamesResult = Collections.singletonList(dummyCountry("Canada"));

        // Act
        interactor.execute(requested);

        // Assert
        assertEquals(requested, dataAccess.lastRequestedNames);
        assertEquals(0, presenter.successCalls);
        assertEquals(1, presenter.failCalls);
        assertEquals("Some selected countries could not be found.", presenter.lastErrorMessage);
    }

    @Test
    void execute_success_when2CountriesFound_callsPrepareSuccessView() {
        // Arrange
        List<String> requested = Arrays.asList("Canada", "France");
        List<Country> found = Arrays.asList(
                dummyCountry("Canada"),
                dummyCountry("France")
        );
        dataAccess.countriesByNamesResult = found;

        // Act
        interactor.execute(requested);

        // Assert data access usage
        assertEquals(requested, dataAccess.lastRequestedNames);

        // Assert presenter usage
        assertEquals(1, presenter.successCalls);
        assertEquals(0, presenter.failCalls);
        assertNotNull(presenter.lastOutputData);

        List<Country> selected = presenter.lastOutputData.getSelectedCountries();
        assertEquals(2, selected.size());
        assertEquals("Canada", selected.get(0).getName());
        assertEquals("France", selected.get(1).getName());

        // Also verify CompareOutputData's list is unmodifiable
        assertThrows(UnsupportedOperationException.class,
                () -> selected.add(dummyCountry("Germany")));
    }

    @Test
    void execute_success_when3CountriesFound() {
        List<String> requested = Arrays.asList("Canada", "France", "Japan");
        List<Country> found = Arrays.asList(
                dummyCountry("Canada"),
                dummyCountry("France"),
                dummyCountry("Japan")
        );
        dataAccess.countriesByNamesResult = found;

        interactor.execute(requested);

        assertEquals(requested, dataAccess.lastRequestedNames);
        assertEquals(1, presenter.successCalls);
        assertEquals(0, presenter.failCalls);
        assertNotNull(presenter.lastOutputData);
        assertEquals(3, presenter.lastOutputData.getSelectedCountries().size());
    }

    @Test
    void execute_success_when4CountriesFound() {
        List<String> requested = Arrays.asList("Canada", "France", "Japan", "Brazil");
        List<Country> found = Arrays.asList(
                dummyCountry("Canada"),
                dummyCountry("France"),
                dummyCountry("Japan"),
                dummyCountry("Brazil")
        );
        dataAccess.countriesByNamesResult = found;

        interactor.execute(requested);

        assertEquals(requested, dataAccess.lastRequestedNames);
        assertEquals(1, presenter.successCalls);
        assertEquals(0, presenter.failCalls);
        assertNotNull(presenter.lastOutputData);
        assertEquals(4, presenter.lastOutputData.getSelectedCountries().size());
    }

    @Test
    void execute_success_when5CountriesFound() {
        List<String> requested = Arrays.asList("Canada", "France", "Japan", "Brazil", "India");
        List<Country> found = Arrays.asList(
                dummyCountry("Canada"),
                dummyCountry("France"),
                dummyCountry("Japan"),
                dummyCountry("Brazil"),
                dummyCountry("India")
        );
        dataAccess.countriesByNamesResult = found;

        interactor.execute(requested);

        assertEquals(requested, dataAccess.lastRequestedNames);
        assertEquals(1, presenter.successCalls);
        assertEquals(0, presenter.failCalls);
        assertNotNull(presenter.lastOutputData);
        assertEquals(5, presenter.lastOutputData.getSelectedCountries().size());
    }

    // ---------------------------------------------------------
    // Test doubles
    // ---------------------------------------------------------

    /**
     * Stub implementation of CompareDataAccessInterface.
     * Behavior is controlled by public fields.
     */
    private static class StubDataAccess implements CompareDataAccessInterface {

        List<String> allCountryNames = new ArrayList<>();
        List<Country> countriesByNamesResult = new ArrayList<>();
        List<String> lastRequestedNames = null;

        @Override
        public List<String> getAllCountryNames() {
            return allCountryNames;
        }

        @Override
        public List<Country> getCountriesByNames(List<String> names) {
            this.lastRequestedNames = new ArrayList<>(names);
            return countriesByNamesResult;
        }
    }

    /**
     * Stub implementation of CompareOutputBoundary.
     * Captures parameters and counts for assertions.
     */
    private static class StubPresenter implements CompareOutputBoundary {

        int prepareCountriesListCalls = 0;
        int successCalls = 0;
        int failCalls = 0;

        List<String> lastCountryNames = null;
        CompareOutputData lastOutputData = null;
        String lastErrorMessage = null;

        @Override
        public void prepareCountriesList(List<String> countryNames) {
            prepareCountriesListCalls++;
            lastCountryNames = (countryNames == null)
                    ? null
                    : new ArrayList<>(countryNames);
        }

        @Override
        public void prepareSuccessView(CompareOutputData outputData) {
            successCalls++;
            lastOutputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failCalls++;
            lastErrorMessage = errorMessage;
        }
    }

    // ---------------------------------------------------------
    // Helper to build dummy Country objects
    // ---------------------------------------------------------

    /**
     * Creates a minimal Country instance for tests.
     * Only the name is asserted in tests; other fields are dummy values.
     */
    private static Country dummyCountry(String name) {
        return new Country(
                "XXX",                  // code
                name,                   // name
                "Capital",              // capital
                "Region",               // region
                "Subregion",            // subregion
                1_000_000,              // population
                12345.67,               // area
                Collections.emptyList(),// borders
                "https://example.com",  // flagUrl
                Collections.emptyList(),// languages
                Collections.emptyList(),// currencies
                Collections.emptyList() // timezones
        );
    }
}