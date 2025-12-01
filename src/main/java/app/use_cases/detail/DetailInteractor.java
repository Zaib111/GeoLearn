package app.use_cases.detail;
import app.entities.Country;

/**
 * Interactor class for the Detail Use Case.
 * Implements the business logic for retrieving and processing country details.
 * Orchestrates the data flow between the Data Access Interface and the Output Boundary.
 */
public class DetailInteractor implements DetailInputBoundary{
    // Interface for retrieving country data from the repository
    private final DetailDataAccessInterface dataAccess;
    // Interface for presenting the output to the view
    private final DetailOutputBoundary output;

    /**
     * Constructor for the Interactor.
     *
     * @param dataAccess The data access object for fetching country entities.
     * @param output     The presenter for handling the output.
     */
    public DetailInteractor(DetailDataAccessInterface dataAccess, DetailOutputBoundary output) {
        this.dataAccess = dataAccess;
        this.output = output;
    }

    /**
     * Executes the detail retrieval logic.
     * Checks if the input corresponds to a country code or name, and prepares the appropriate view.
     *
     * @param detailInputData The input data containing the identifier.
     */
    @Override
    public void execute(DetailInputData detailInputData){
        // Retrieve the raw search string
        final String countryInfo = detailInputData.getCountryInfo();

        // Attempt to find the country assuming the input is a Country Code
        if(dataAccess.getCountryByCode(countryInfo) != null){
            final Country country = dataAccess.getCountryByCode(countryInfo);
            // Create output data and trigger success view
            DetailOutputData outputData = new DetailOutputData(country);
            output.prepareDetailSuccessView(outputData);
        }
        // If not found by code, attempt to find the country assuming the input is a Country Name
        else if(dataAccess.getCountryByName(countryInfo) != null){
            final Country country = dataAccess.getCountryByName(countryInfo);
            // Create output data and trigger success view
            DetailOutputData outputData = new DetailOutputData(country);
            output.prepareDetailSuccessView(outputData);
        }
        // If the country cannot be found by either code or name
        else{
            final String errorMessage = String.format("Country %s not found", countryInfo);
            // Trigger the failure view with an error message
            output.prepareDetailFailureView(errorMessage);
        }
    }
}