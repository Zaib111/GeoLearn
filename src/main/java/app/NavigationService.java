package app;

/**
 * Service interface for navigation between views in the application.
 * This interface segregates navigation functionality from other MasterFrame concerns,
 * allowing views to depend only on what they need.
 */
public interface NavigationService {
    /**
     * Navigates to the view with the specified name.
     *
     * @param name the name of the view to navigate to
     */
    void navigateTo(String name);

    /**
     * Navigates to the view with the specified name and parameter.
     *
     * @param name the name of the view to navigate to
     * @param param the parameter to pass to the view
     */
    void navigateTo(String name, String param);

    /**
     * Controls the visibility of the global back button in the MasterFrame.
     * Views can use this to temporarily hide the back button when they provide
     * their own in-view navigation (e.g., CompareView's "Back to Country Selection").
     *
     * @param visible true to show the back button, false to hide it
     */
    void setBackButtonVisible(boolean visible);
}
