package app.use_cases.explore_map;

import java.io.IOException;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Coordinate;

/**
 * Interactor for the Explore Map use case.
 * This class coordinates between the data access object and the presenter,
 * executing business rules when a map is loaded or a feature on the map
 * is selected.
 */
public class ExploreMapInteractor implements ExploreMapInputBoundary {

    /** Handles loading and querying map feature data. */
    private final ExploreMapDataAccessInterface dataAccess;

    /** Prepares output data and updates the view through the presenter. */
    private final ExploreMapOutputBoundary presenter;
    /**

    /**
     * Constructs an ExploreMapInteractor with the required dependency interfaces.
     *
     * @param dataAccess the data access interface for map/geospatial data
     * @param presenter the presenter used to format and return output to the UI
     */
    public ExploreMapInteractor(ExploreMapDataAccessInterface dataAccess,
                                ExploreMapOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    /**
     * Loads a map using the provided input data and triggers the presenter
     * to update the UI with a successful or failure result.
     *
     * @param inputData contains the path to the shapefile to load
     */
    @Override
    public void loadMap(ExploreMapInputData inputData) {
        try {
            // Load the shapefile into a feature source object
            final SimpleFeatureSource featureSource =
                    dataAccess.loadShapefile(inputData.getShapefilePath());

            // Store the loaded map for future interactions
            dataAccess.setFeatureSource(featureSource);

            // Notify presenter that the map has been successfully loaded
            final ExploreMapOutputData outputData =
                    new ExploreMapOutputData(featureSource, null, null);
            presenter.prepareMapLoadedView(outputData);
        }
        catch (IOException ioException) {
            // Provide user-friendly failure message on load error
            presenter.prepareFailView("Error loading map: " + ioException.getMessage());
        }
    }

    /**
     * Selects a feature on the map using the screen coordinate input, retrieves
     * the associated country name if present, and informs the presenter of the result.
     *
     * @param inputData the screen coordinates of the feature selection event
     */
    @Override
    public void selectFeature(ExploreMapSelectInputData inputData) {
        // Convert screen coordinates into a geospatial coordinate object
        final Coordinate coordinate = new Coordinate(inputData.getX(), inputData.getY());

        // Attempt to retrieve the feature at the selected location
        final SimpleFeature feature = dataAccess.getFeatureAtPosition(coordinate);

        // The displayed name defaults to null if no feature is found
        String countryName = null;
        if (feature != null) {
            // Attempt to read the country name attribute from the feature
            final Object nameAttribute = feature.getAttribute("NAME");
            countryName = (nameAttribute != null)
                    ? nameAttribute.toString()
                    : feature.getID();

        }

        // Send selected feature details to the presenter for display
        final ExploreMapOutputData outputData =
                new ExploreMapOutputData(null, feature, countryName);
        presenter.prepareFeatureSelectedView(outputData);
    }
}
