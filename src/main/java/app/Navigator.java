package app;

/**
 * Navigator handles navigation between views in the application.
 */
public class Navigator {
    private final MasterFrame masterFrame;

    /**
     * Constructs a Navigator with the specified master frame.
     *
     * @param masterFrame the master frame to use for navigation
     */
    public Navigator(MasterFrame masterFrame) {
        this.masterFrame = masterFrame;
    }

    /**
     * Navigates to the view with the specified name.
     *
     * @param name the name of the view to navigate to
     */
    public void navigateTo(String name) {
        masterFrame.navigateTo(name, "");
    }

    /**
     * Navigates to the view with the specified name and parameter.
     *
     * @param name the name of the view to navigate to
     * @param param the parameter to pass to the view
     */
    public void navigateTo(String name, String param) {
        masterFrame.navigateTo(name, param);
    }
}
