package app.use_cases.detail;
import app.use_cases.country.CountryDataAccessInterface;

public class DetailInteractor implements DetailInputBoundary{
    private final DetailDataAccessInterface dataAccess;
    private final DetailOutputBoundary output;
    public DetailInteractor(DetailDataAccessInterface dataAccess, DetailOutputBoundary output) {
        this.dataAccess = dataAccess;
        this.output = output;
    }
    @Override
    public void execute(DetailInputData detailInputData){
        final String countryName = detailInputData.getCountryName();
        /*if(!dataAccess.getCountries().contains(countryCode)){
            output.prepareFailureView("Country Does Not Exist");
        }
        else{
            DetailOutputData outputData = new DetailOutputData(dataAccess.getCountryByCode(countryCode));
            output.prepareDetailView(outputData);
        }*/
        DetailOutputData outputData = new DetailOutputData(dataAccess.getCountryByName(countryName));
        output.prepareDetailView(outputData);
    }
}
