package app.use_cases.detail;
import app.use_cases.country.CountryDataAccessInterface;

public class DetailInteractor implements DetailInputBoundary{
    private final CountryDataAccessInterface dataAccess;
    private final DetailOutputBoundary output;
    public DetailInteractor(CountryDataAccessInterface dataAccess, DetailOutputBoundary output) {
        this.dataAccess = dataAccess;
        this.output = output;
    }
    @Override
    public void execute(DetailInputData detailInputData){
        final String countryCode = detailInputData.getCountryCode();
        if(!dataAccess.getCountries().contains(countryCode)){
            output.prepareFailureView("Country Does Not Exist");
        }
        else{
            DetailOutputData outputData = new DetailOutputData(dataAccess.getCountry(countryCode));
            output.prepareSuccessView(outputData);
        }
    }
}
