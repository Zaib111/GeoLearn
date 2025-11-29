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
        final String countryInfo = detailInputData.getCountryInfo();
        if(dataAccess.getCountryByCode(countryInfo) != null){
            final Country country = dataAccess.getCountryByCode(countryInfo);
            DetailOutputData outputData = new DetailOutputData(country);
            output.prepareDetailSuccessView(outputData);
        }
        else if(dataAccess.getCountryByName(countryInfo) != null){
            final Country country = dataAccess.getCountryByName(countryInfo);
            DetailOutputData outputData = new DetailOutputData(country);
            output.prepareDetailSuccessView(outputData);
        }

    }
}
