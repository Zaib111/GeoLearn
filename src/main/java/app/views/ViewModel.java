package app.views;

import java.util.ArrayList;
import java.util.List;

public class ViewModel<ViewState> {

    private ViewState state;

    private List<AbstractView> subscribedViews = new ArrayList<>();

    public ViewModel(ViewState initialState) {
        this.state = initialState;
    }

    public ViewState getState() {
        return this.state;
    }

    public void updateState(ViewState state) {
        ViewState oldState = this.state;
        this.state = state;
        subscribedViews.forEach(view -> {
            view.onStateChange(oldState, state);
        });
    }

    public void subscribeToStateUpdateEvents(AbstractView view) {
        subscribedViews.add(view);
    }
}
