package app;

import adapters.ExploreMap.ExploreMapController;
import adapters.ExploreMap.ExploreMapPresenter;
import adapters.ExploreMap.ExploreMapViewModel;
import adapters.ViewManagerModel;
import data_access.ExploreMapDataAccessObject;
import use_case.explore_map.ExploreMapInteractor;
import view.ExploreMapView;

import javax.swing.*;

/**
 * Demo application for the ExploreMap feature using Clean Architecture.
 */
public class ExploreMapDemo {
    public static void main(String[] args) {
        // Set system properties for better rendering
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.opengl", "false");

        SwingUtilities.invokeLater(() -> {
            // Create the application frame
            JFrame frame = new JFrame("GeoLearn - Explore Map (Clean Architecture)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);

            // Build the application using Clean Architecture
            ExploreMapView view = buildExploreMapUseCase();

            frame.add(view);
            frame.setVisible(true);
        });
    }

    /**
     * Builds the ExploreMap use case following Clean Architecture principles.
     * This method wires together all the layers:
     * - Data Access Layer (DAO)
     * - Use Case Layer (Interactor)
     * - Interface Adapters Layer (Controller, Presenter, ViewModel)
     * - View Layer (UI)
     */
    private static ExploreMapView buildExploreMapUseCase() {
        // 1. Create the Data Access Object (Infrastructure Layer)
        ExploreMapDataAccessObject dataAccess = new ExploreMapDataAccessObject();

        // 2. Create the ViewModel (Interface Adapters Layer)
        ExploreMapViewModel viewModel = new ExploreMapViewModel();
        ViewManagerModel viewManagerModel = new ViewManagerModel();

        // 3. Create the Presenter (Interface Adapters Layer)
        ExploreMapPresenter presenter = new ExploreMapPresenter(viewModel);

        // 4. Create the Interactor (Use Case Layer)
        ExploreMapInteractor interactor = new ExploreMapInteractor(dataAccess, presenter);

        // 5. Create the Controller (Interface Adapters Layer)
        ExploreMapController controller = new ExploreMapController(interactor);

        // 6. Create the View (UI Layer) and wire everything together
        ExploreMapView view = new ExploreMapView(viewModel);
        view.setController(controller);

        return view;
    }
}
