package app.use_cases.detail;

import app.entities.Country;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Add imports for JUnit
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class DetailInteractorTest {

    private TestDetailDataAccess dataAccess;
    private TestDetailPresenter presenter;
    private DetailInteractor interactor;
    private static class TestDetailDataAccess implements DetailDataAccessInterface {
        List<Country> countries = new ArrayList<>();
        @Override
        public Country getCountryByCode(String code){
            for (Country country : countries) {
                if (country.getCode().equals(code)) {
                    return country;
                }
            }
            return null;
        }
        @Override
        public Country getCountryByName(String name){
            for (Country country : countries) {
                if (country.getName().equals(name)) {
                    return country;
                }
            }
            return null;
        }

    }

    private static class TestDetailPresenter implements DetailOutputBoundary {
        boolean successCalled = false;
        DetailOutputData receivedOutputData;
        boolean failureCalled = false;
        String receivedError;

        @Override
        public void prepareDetailSuccessView(DetailOutputData outputData) {
            successCalled = true;
            receivedOutputData = outputData;
        }

        @Override
        public void prepareDetailFailureView(String errorMessage) {
            failureCalled = true;
            receivedError = errorMessage;
        }
    }


    private static Country testCountryByName(String name) {
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

    private static Country testCountryByCode(String code) {
        return new Country(
                code,                  // code
                "Name",                   // name
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

    @BeforeEach
    void setUp() {
        dataAccess = new TestDetailDataAccess();
        presenter = new TestDetailPresenter();
        interactor = new DetailInteractor(dataAccess, presenter);
    }

    @Test
    void testExecuteSuccessByCode() {
        String query = "XXX";
        Country expectedCountry = testCountryByCode(query);
        dataAccess.countries.add(expectedCountry);

        interactor.execute(new DetailInputData(query));

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failureCalled);
        assertEquals(expectedCountry.getCode(), presenter.receivedOutputData.getCountryCode());
        assertEquals(expectedCountry.getName(), presenter.receivedOutputData.getCountryName());
        assertEquals(expectedCountry.getCapital(), presenter.receivedOutputData.getCapital());
        assertEquals(expectedCountry.getRegion(), presenter.receivedOutputData.getRegion());
        assertEquals(expectedCountry.getSubregion(), presenter.receivedOutputData.getSubregion());
        assertEquals(expectedCountry.getAreaKm2(), presenter.receivedOutputData.getAreaKm2());
        assertEquals(expectedCountry.getPopulation(), presenter.receivedOutputData.getPopulation());
        assertEquals(expectedCountry.getFlagUrl(), presenter.receivedOutputData.getFlagUrl());
        assertEquals(expectedCountry.getTimezones(), presenter.receivedOutputData.getTimezones());
        assertEquals(expectedCountry.getCurrencies(), presenter.receivedOutputData.getCurrencies());
        assertEquals(expectedCountry.getLanguages(), presenter.receivedOutputData.getLanguages());
        assertEquals(expectedCountry.getBorders(), presenter.receivedOutputData.getBorders());
    }

    @Test
    void testExecuteSuccessByName() {
        String query = "TestCountry";
        Country expectedCountry = testCountryByName(query);
        dataAccess.countries.add(expectedCountry);

        interactor.execute(new DetailInputData(query));

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failureCalled);
        assertEquals(expectedCountry.getCode(), presenter.receivedOutputData.getCountryCode());
        assertEquals(expectedCountry.getName(), presenter.receivedOutputData.getCountryName());
    }

    @Test
    void testExecuteFailure() {
        String query = "NonExistent";
        interactor.execute(new DetailInputData(query));

        assertFalse(presenter.successCalled);
        assertTrue(presenter.failureCalled);
        assertEquals(String.format("Country %s not found", query), presenter.receivedError);
    }

    @Test
    void testExecutePrefersCodeOverName() {
        // Test to show priority: if query matches both a code and a name, prefers code
        String query = "XXX";
        Country countryByCode = testCountryByCode(query);
        Country countryByName = testCountryByName(query);
        dataAccess.countries.add(countryByCode);
        dataAccess.countries.add(countryByName);

        interactor.execute(new DetailInputData(query));

        assertTrue(presenter.successCalled);
        assertFalse(presenter.failureCalled);
        assertEquals(countryByCode.getCode(), presenter.receivedOutputData.getCountryCode());
        assertEquals(countryByCode.getName(), presenter.receivedOutputData.getCountryName());
    }

}
