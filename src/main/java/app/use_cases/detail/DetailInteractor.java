package app.use_cases.detail;

import app.entities.Country;

/**
 * Interactor for detail use case.
 */
public class DetailInteractor implements DetailInputBoundary {
    private final DetailDataAccessInterface dataAccess;
    private final DetailOutputBoundary output;

    /**
     * Constructor.
     * @param dataAccess the data access interface
     * @param output the output boundary
     */
    public DetailInteractor(final DetailDataAccessInterface dataAccess,
                           final DetailOutputBoundary output) {
        this.dataAccess = dataAccess;
        this.output = output;
    }

    @Override
    public void execute(final DetailInputData detailInputData) {
        final String countryCode = detailInputData.getCountryCode();
        final Country country = dataAccess.getCountryByCode(countryCode);
        final DetailOutputData outputData = new DetailOutputData(country);
        output.prepareDetailSuccessView(outputData);
    }
}
