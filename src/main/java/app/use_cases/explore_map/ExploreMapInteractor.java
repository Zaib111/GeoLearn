package app.use_cases.explore_map;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.locationtech.jts.geom.Coordinate;

/**
 * Interactor for the Explore Map use case.
 * Only handles data operations - no UI state management.
 */
public class ExploreMapInteractor implements ExploreMapInputBoundary {

    private final ExploreMapDataAccessInterface dataAccess;
    private final ExploreMapOutputBoundary presenter;

    public ExploreMapInteractor(ExploreMapDataAccessInterface dataAccess,
                                ExploreMapOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void loadMap(ExploreMapInputData inputData) {
        try {
            SimpleFeatureSource featureSource = dataAccess.loadShapefile(inputData.getShapefilePath());
            dataAccess.setFeatureSource(featureSource);

            ExploreMapOutputData outputData = new ExploreMapOutputData(
                featureSource, null, null
            );
            presenter.prepareMapLoadedView(outputData);
        } catch (Exception e) {
            presenter.prepareFailView("Error loading map: " + e.getMessage());
        }
    }

    @Override
    public void selectFeature(ExploreMapSelectInputData inputData) {
        Coordinate coord = new Coordinate(inputData.getX(), inputData.getY());
        SimpleFeature feature = dataAccess.getFeatureAtPosition(coord);

        String countryName = null;
        if (feature != null) {
            try {
                Object nameAttr = feature.getAttribute("NAME");
                countryName = nameAttr != null ? nameAttr.toString() : feature.getID();
            } catch (Exception e) {
                countryName = feature.getID();
            }
        }

        ExploreMapOutputData outputData = new ExploreMapOutputData(
            null, feature, countryName
        );
        presenter.prepareFeatureSelectedView(outputData);
    }
}
