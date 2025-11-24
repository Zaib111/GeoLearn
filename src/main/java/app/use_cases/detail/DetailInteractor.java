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
        final String countryName = detailInputData.getCountryName();
        final Country country = dataAccess.getCountryByName(countryName);
        /*if(!dataAccess.getCountries().contains(country)){
            output.prepareDetailFailureView("Country Does Not Exist");
        }
        else{
            DetailOutputData outputData = new DetailOutputData(dataAccess.getCountryByName(countryName));
            output.prepareDetailSuccessView(outputData);
        }*/
        DetailOutputData outputData = new DetailOutputData(country);
        output.prepareDetailSuccessView(outputData);
    }
}
