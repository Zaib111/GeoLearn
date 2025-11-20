package app.views;

import javax.swing.*;

public abstract class AbstractView extends JPanel {
    protected final ViewModel<?> viewModel;

    protected AbstractView(ViewModel<?> viewModel) {
        this.viewModel = viewModel;
        viewModel.subscribeToStateUpdateEvents(this);
    }

    public abstract void onViewOpened();

    public abstract void onViewClosed();

    public abstract void onStateChange(Object oldState, Object newState);
}
