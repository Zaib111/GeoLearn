package app.views;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic view model that manages state and notifies subscribed views of state changes.
 *
 * @param <S> the type of state this view model manages
 */
public class ViewModel<S> {

    private S state;

    private final List<AbstractView> subscribedViews = new ArrayList<>();

    /**
     * Constructs a ViewModel with the specified initial state.
     *
     * @param initialState the initial state of the view model
     */
    public ViewModel(S initialState) {
        this.state = initialState;
    }

    /**
     * Gets the current state.
     *
     * @return the current state
     */
    public S getState() {
        return this.state;
    }

    /**
     * Updates the state and notifies all subscribed views.
     *
     * @param newState the new state to set
     */
    public void updateState(S newState) {
        final S oldState = this.state;
        this.state = newState;
        subscribedViews.forEach(view -> {
            view.onStateChange(oldState, newState);
        });
    }

    /**
     * Subscribes a view to state update events.
     *
     * @param view the view to subscribe
     */
    public void subscribeToStateUpdateEvents(AbstractView view) {
        subscribedViews.add(view);
    }
}
