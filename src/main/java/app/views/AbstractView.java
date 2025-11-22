package app.views;

import javax.swing.JPanel;

/**
 * Abstract base class for all views in the application.
 * Manages view lifecycle and state change notifications.
 */
public abstract class AbstractView extends JPanel {
    private final ViewModel<?> viewModel;

    /**
     * Constructs an AbstractView with the specified view model.
     *
     * @param viewModel the view model to associate with this view
     */
    protected AbstractView(ViewModel<?> viewModel) {
        this.viewModel = viewModel;
        viewModel.subscribeToStateUpdateEvents(this);
    }

    /**
     * Gets the view model.
     *
     * @return the view model
     */
    protected ViewModel<?> getViewModel() {
        return viewModel;
    }

    /**
     * Called when the view is opened.
     */
    public abstract void onViewOpened();

    /**
     * Called when the view is closed.
     */
    public abstract void onViewClosed();

    /**
     * Called when the view model state changes.
     *
     * @param oldState the previous state
     * @param newState the new state
     */
    public abstract void onStateChange(Object oldState, Object newState);
}
