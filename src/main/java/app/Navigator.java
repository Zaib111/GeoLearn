package app;

public class Navigator {
    MasterFrame masterFrame;

    void subscribeToNavigationEvents(MasterFrame masterFrame) {
        this.masterFrame  = masterFrame;
    }

    public void navigateTo(String name) {
        masterFrame.navigateTo(name);
    }
}

