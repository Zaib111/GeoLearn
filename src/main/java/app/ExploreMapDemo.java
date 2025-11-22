package app;

import app.controllers.ExploreMapController;
import app.presenters.ExploreMapPresenter;
import app.views.explore_map.ExploreMapState;
import app.data_access.ExploreMapDataAccessObject;
import app.use_cases.explore_map.ExploreMapInteractor;
import app.views.ViewModel;
import app.views.explore_map.ExploreMapView;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Demo application for the ExploreMap feature using Clean Architecture.
 */
public final class ExploreMapDemo {
    /** Width of the application frame. */
    private static final int FRAME_WIDTH = 1200;
    /** Height of the application frame. */
    private static final int FRAME_HEIGHT = 800;

    private ExploreMapDemo() {
        // Utility class - prevent instantiation
    }

    /**
     * Main method to launch the ExploreMap demo application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(final String[] args) {
        System.setProperty("sun.java2d.d3d", "false");
        System.setProperty("sun.java2d.noddraw", "true");
        System.setProperty("sun.java2d.opengl", "false");

        SwingUtilities.invokeLater(() -> {
            // Create the application frame
            final JFrame frame = new JFrame(
                    "GeoLearn - Explore Map (Clean Architecture)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
            frame.setLocationRelativeTo(null);

            // Build the application using Clean Architecture
            final ExploreMapView view = buildExploreMapUseCase();

            frame.add(view);
            frame.setVisible(true);
        });
    }

    /**
     * Builds the ExploreMap use case following Clean Architecture.
     * This method wires together all the layers:
     * - Data Access Layer (DAO)
     * - Use Case Layer (Interactor)
     * - Interface Adapters Layer (Controller, Presenter, ViewModel)
     * - View Layer (UI)
     *
     * @return the constructed ExploreMapView
     */
    private static ExploreMapView buildExploreMapUseCase() {
        // 1. Create the Data Access Object (Infrastructure Layer)
        final ExploreMapDataAccessObject dataAccess =
                new ExploreMapDataAccessObject();

        // 2. Create the ViewModel (Interface Adapters Layer)
        final ViewModel<ExploreMapState> viewModel = new ViewModel<>(new ExploreMapState());

        // 3. Create the Presenter (Interface Adapters Layer)
        final ExploreMapPresenter presenter =
                new ExploreMapPresenter(viewModel);

        // 4. Create the Interactor (Use Case Layer)
        final ExploreMapInteractor interactor =
                new ExploreMapInteractor(dataAccess, presenter);

        // 5. Create the Controller (Interface Adapters Layer)
        final ExploreMapController controller =
                new ExploreMapController(interactor);

        // 6. Create the View (UI Layer) and wire everything together
        final ExploreMapView view = new ExploreMapView(viewModel);
        view.setController(controller);

        return view;
    }
}
