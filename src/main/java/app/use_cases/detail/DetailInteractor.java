package app.use_cases.detail;
import app.entities.Country;

public class DetailInteractor implements DetailInputBoundary{
    private final DetailDataAccessInterface dataAccess;
    private final DetailOutputBoundary output;
    public DetailInteractor(DetailDataAccessInterface dataAccess, DetailOutputBoundary output) {
        this.dataAccess = dataAccess;
        this.output = output;
    }
    @Override
    public void execute(DetailInputData detailInputData){
        final String countryCode = detailInputData.getCountryCode();
        final Country country = dataAccess.getCountryByCode(countryCode);
        DetailOutputData outputData = new DetailOutputData(country);
        output.prepareDetailSuccessView(outputData);
    }
}
