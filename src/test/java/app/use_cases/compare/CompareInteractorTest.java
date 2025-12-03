package app.use_cases.compare;

import app.entities.Country;
import app.views.compare.CompareState;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Single test file that covers BOTH CompareInteractor and CompareViewModel.
 */
class CompareInteractorTest {

    // ====== Test doubles for the interactor ======

    /**
     * Simple fake DataAccess that can be configured per test.
     */
    private static class FakeDataAccess implements CompareDataAccessInterface {
        List<String> namesToReturn = Collections.emptyList();
        List<Country> countriesToReturn = Collections.emptyList();

        @Override
        public List<String> getAllCountryNames() {
            return namesToReturn;
        }

        @Override
        public List<Country> getCountriesByNames(List<String> names) {
            return countriesToReturn;
        }
    }

    /**
     * Presenter that records which method was called and with what.
     */
    private static class RecordingPresenter implements CompareOutputBoundary {
        List<String> lastCountryNames;
        CompareOutputData lastOutputData;
        String lastErrorMessage;

        int countriesListCallCount = 0;
        int successCallCount = 0;
        int failCallCount = 0;

        @Override
        public void prepareCountriesList(List<String> countryNames) {
            countriesListCallCount++;
            this.lastCountryNames = countryNames;
        }

        @Override
        public void prepareSuccessView(CompareOutputData outputData) {
            successCallCount++;
            this.lastOutputData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failCallCount++;
            this.lastErrorMessage = errorMessage;
        }
    }

    // ====== Tests for CompareInteractor ======

    @Test
    void loadAvailableCountries_success_callsPrepareCountriesList() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        dataAccess.namesToReturn = Arrays.asList("Canada", "Japan", "Brazil");

        RecordingPresenter presenter = new RecordingPresenter();
        CompareInteractor interactor = new CompareInteractor(dataAccess, presenter);

        interactor.loadAvailableCountries();

        assertEquals(1, presenter.countriesListCallCount);
        assertEquals(0, presenter.failCallCount);
        assertEquals(Arrays.asList("Canada", "Japan", "Brazil"), presenter.lastCountryNames);
    }

    @Test
    void loadAvailableCountries_failure_whenListIsNullOrEmpty() {
        // Case 1: null list
        CompareDataAccessInterface nullDataAccess = new CompareDataAccessInterface() {
            @Override
            public List<String> getAllCountryNames() {
                return null; // triggers the null branch
            }

            @Override
            public List<Country> getCountriesByNames(List<String> names) {
                return Collections.emptyList();
            }
        };

        RecordingPresenter presenterNull = new RecordingPresenter();
        CompareInteractor interactorNull = new CompareInteractor(nullDataAccess, presenterNull);

        interactorNull.loadAvailableCountries();

        assertEquals(0, presenterNull.countriesListCallCount);
        assertEquals(1, presenterNull.failCallCount);
        assertEquals("Failed to load country list.", presenterNull.lastErrorMessage);

        // Case 2: empty list
        FakeDataAccess emptyDataAccess = new FakeDataAccess();
        emptyDataAccess.namesToReturn = Collections.emptyList();

        RecordingPresenter presenterEmpty = new RecordingPresenter();
        CompareInteractor interactorEmpty = new CompareInteractor(emptyDataAccess, presenterEmpty);

        interactorEmpty.loadAvailableCountries();

        assertEquals(0, presenterEmpty.countriesListCallCount);
        assertEquals(1, presenterEmpty.failCallCount);
        assertEquals("Failed to load country list.", presenterEmpty.lastErrorMessage);
    }

    @Test
    void execute_failure_whenSelectedNamesIsNull() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        RecordingPresenter presenter = new RecordingPresenter();
        CompareInteractor interactor = new CompareInteractor(dataAccess, presenter);

        interactor.execute(null);

        assertEquals(1, presenter.failCallCount);
        assertEquals(0, presenter.successCallCount);
        assertEquals("Select at least two countries to compare.", presenter.lastErrorMessage);
    }

    @Test
    void execute_failure_whenLessThanTwoCountriesSelected() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        RecordingPresenter presenter = new RecordingPresenter();
        CompareInteractor interactor = new CompareInteractor(dataAccess, presenter);

        interactor.execute(Collections.singletonList("Canada"));

        assertEquals(1, presenter.failCallCount);
        assertEquals(0, presenter.successCallCount);
        assertEquals("Select at least two countries to compare.", presenter.lastErrorMessage);
    }

    @Test
    void execute_failure_whenSomeCountriesNotFound() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        // Two names will be passed in, but we only return one Country → triggers mismatch branch
        dataAccess.countriesToReturn = Collections.singletonList((Country) null);

        RecordingPresenter presenter = new RecordingPresenter();
        CompareInteractor interactor = new CompareInteractor(dataAccess, presenter);

        interactor.execute(Arrays.asList("Canada", "Japan"));

        assertEquals(1, presenter.failCallCount);
        assertEquals(0, presenter.successCallCount);
        assertEquals("Some selected countries could not be found.", presenter.lastErrorMessage);
    }

    @Test
    void execute_success_whenAllCountriesFound() {
        FakeDataAccess dataAccess = new FakeDataAccess();
        List<Country> fakeCountries = Arrays.asList((Country) null, (Country) null);
        dataAccess.countriesToReturn = fakeCountries;

        RecordingPresenter presenter = new RecordingPresenter();
        CompareInteractor interactor = new CompareInteractor(dataAccess, presenter);

        interactor.execute(Arrays.asList("Canada", "Japan"));

        assertEquals(0, presenter.failCallCount);
        assertEquals(1, presenter.successCallCount);
        assertNotNull(presenter.lastOutputData);
        assertEquals(fakeCountries, presenter.lastOutputData.getSelectedCountries());
    }

    // ====== Tests for CompareViewModel ======

    /**
     * Subclass exposing the protected copyState method.
     */
    private static class TestableCompareViewModel extends CompareViewModel {
        public CompareState callCopyState(CompareState state) {
            return super.copyState(state);
        }
    }

    @Test
    void compareViewModel_constructor_initializesWithoutError() {
        // Hits CompareViewModel constructor line: super(new CompareState());
        CompareViewModel viewModel = new CompareViewModel();
        assertNotNull(viewModel);
    }

    @Test
    void compareViewModel_copyState_copiesAllFieldsCorrectly() {
        // Build a CompareState with actual values so all getters are exercised.
        List<String> countryNames = Arrays.asList("Canada", "Japan");
        String[] columnHeaders = {"Name", "Population"};
        Object[][] comparisonTableData = {
                {"Canada", "38M"},
                {"Japan", "125M"}
        };
        List<Country> selectedCountries = Arrays.asList((Country) null, (Country) null);
        String errorMessage = "Some error";

        CompareState original = new CompareState(
                countryNames,
                columnHeaders,
                comparisonTableData,
                selectedCountries,
                errorMessage
        );

        TestableCompareViewModel vm = new TestableCompareViewModel();

        // Directly hits CompareViewModel.copyState(...)
        CompareState copy = vm.callCopyState(original);

        // New instance with same data
        assertNotSame(original, copy);
        assertEquals(countryNames, copy.getCountryNames());
        assertArrayEquals(columnHeaders, copy.getColumnHeaders());
        assertArrayEquals(comparisonTableData, copy.getComparisonTableData());
        assertEquals(selectedCountries, copy.getSelectedCountries());
        assertEquals(errorMessage, copy.getErrorMessage());
    }
}
